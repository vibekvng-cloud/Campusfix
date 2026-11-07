package com.example.data.repository

import com.example.data.local.CampusFixDatabase
import com.example.data.local.CampusRoomEntity
import com.example.data.local.FaultReportEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.CampusRoom
import com.example.data.model.FaultReport
import com.example.data.model.MaintenanceTeam
import com.example.data.model.OnlineWorker
import com.example.data.model.ReportStatus
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class CampusFixRepository(private val database: CampusFixDatabase) {
  private val reportDao = database.faultReportDao()
  private val roomDao = database.campusRoomDao()
  private val profileDao = database.userProfileDao()

  // Dynamic Online Workers List for Admin Dispatch
  private val _onlineWorkers = MutableStateFlow(
    listOf(
      OnlineWorker("w_1", "Ing. Emmanuel Addo", "STAFF/0891", "Electrical", true, 2, "EA"),
      OnlineWorker("w_2", "Kofi Mensah", "STAFF/0512", "Plumbing", true, 1, "KM"),
      OnlineWorker("w_3", "Isaac Osei", "STAFF/0324", "IT & AV", true, 0, "IO"),
      OnlineWorker("w_4", "Samuel Appiah", "STAFF/0198", "Carpentry", true, 1, "SA"),
      OnlineWorker("w_5", "Yaw Boateng", "STAFF/0743", "Cooling & AC", true, 0, "YB"),
      OnlineWorker("w_6", "Kwame Baffoe", "STAFF/0621", "General Maintenance", false, 0, "KB")
    )
  )
  val onlineWorkers: Flow<List<OnlineWorker>> = _onlineWorkers.asStateFlow()

  val allReports: Flow<List<FaultReport>> =
    reportDao.getAllReports().map { list -> list.map { it.toModel() } }

  val allRooms: Flow<List<CampusRoom>> =
    roomDao.getAllRooms().map { list -> list.map { it.toModel() } }

  val currentProfile: Flow<UserProfile?> =
    profileDao.getCurrentProfile().map { it?.toModel() }

  fun getReportsForUser(userId: String): Flow<List<FaultReport>> =
    reportDao.getReportsByReporter(userId).map { list -> list.map { it.toModel() } }

  suspend fun submitReport(report: FaultReport): String {
    val entity = FaultReportEntity.fromModel(report)
    reportDao.insertReport(entity)
    roomDao.incrementFaultCount(report.roomCode)
    return report.id
  }

  suspend fun assignTeam(reportId: String, team: MaintenanceTeam, technician: String?) {
    val existing = reportDao.getReportById(reportId) ?: return
    val updated = existing.copy(
      assignedTeam = team,
      assignedTechnician = technician ?: team.leadName,
      status = ReportStatus.ASSIGNED
    )
    reportDao.updateReport(updated)
  }

  suspend fun assignReportToWorker(reportId: String, worker: OnlineWorker) {
    val existing = reportDao.getReportById(reportId) ?: return
    val matchedTeam = when {
      worker.team.contains("Elec", ignoreCase = true) -> MaintenanceTeam.ELECTRICAL
      worker.team.contains("Plumb", ignoreCase = true) -> MaintenanceTeam.PLUMBING
      worker.team.contains("IT", ignoreCase = true) || worker.team.contains("AV", ignoreCase = true) -> MaintenanceTeam.IT_AV
      worker.team.contains("Carp", ignoreCase = true) -> MaintenanceTeam.CARPENTRY
      worker.team.contains("Cool", ignoreCase = true) || worker.team.contains("AC", ignoreCase = true) -> MaintenanceTeam.ELECTRICAL
      else -> MaintenanceTeam.ELECTRICAL
    }
    val updated = existing.copy(
      assignedTeam = matchedTeam,
      assignedTechnician = "${worker.name} (${worker.staffCode})",
      status = ReportStatus.ASSIGNED
    )
    reportDao.updateReport(updated)

    // Increment worker's active job counter
    _onlineWorkers.value = _onlineWorkers.value.map {
      if (it.id == worker.id) it.copy(activeJobsCount = it.activeJobsCount + 1) else it
    }
  }

  suspend fun updateReportStatus(
    reportId: String,
    newStatus: ReportStatus,
    notes: String? = null
  ) {
    val existing = reportDao.getReportById(reportId) ?: return
    val resolvedAt = if (newStatus == ReportStatus.RESOLVED) System.currentTimeMillis() else existing.resolvedAtMillis
    val updatedNotes = notes ?: existing.resolutionNotes
    val updated = existing.copy(
      status = newStatus,
      resolvedAtMillis = resolvedAt,
      resolutionNotes = updatedNotes
    )
    reportDao.updateReport(updated)
  }

  suspend fun deleteReport(reportId: String) {
    reportDao.deleteReport(reportId)
  }

  suspend fun resolveRoomFromQr(qrPayload: String): CampusRoom? {
    val clean = qrPayload.trim()
    val byPayload = roomDao.getRoomByQrPayload(clean)
    if (byPayload != null) return byPayload.toModel()

    val code = if (clean.contains("://room/")) {
      clean.substringAfter("://room/")
    } else clean

    val byCode = roomDao.getRoomByCode(code.uppercase())
    return byCode?.toModel()
  }

  suspend fun signInOrUpdateProfile(
    studentOrStaffId: String,
    fullName: String,
    email: String,
    role: UserRole,
    department: String
  ): UserProfile {
    val cleanId = studentOrStaffId.trim()
    val cleanName = fullName.trim().ifBlank {
      when (role) {
        UserRole.WORKER -> "UCC Works Technician"
        UserRole.ADMIN -> "Directorate Admin"
        UserRole.GUEST -> "Campus Visitor"
        UserRole.STUDENT -> "UCC Student"
      }
    }
    val cleanEmail = email.trim().ifBlank {
      when (role) {
        UserRole.WORKER -> "works.ucc@gmail.com"
        UserRole.ADMIN -> "admin.works@ucc.edu.gh"
        UserRole.GUEST -> "guest.visitor@ucc.edu.gh"
        UserRole.STUDENT -> "student@stu.ucc.edu.gh"
      }
    }
    val cleanDept = department.trim().ifBlank {
      when (role) {
        UserRole.WORKER -> "Works Directorate (Staff)"
        UserRole.ADMIN -> "Directorate of Physical Development"
        UserRole.GUEST -> "Campus Guest Access"
        UserRole.STUDENT -> "UCC Undergraduate Campus"
      }
    }

    val profile = UserProfile(
      id = "user_${cleanId.replace("[^A-Za-z0-9]".toRegex(), "_").lowercase()}",
      studentOrStaffId = cleanId,
      fullName = cleanName,
      email = cleanEmail,
      role = role,
      department = cleanDept
    )
    profileDao.clearProfiles()
    profileDao.insertProfile(UserProfileEntity.fromModel(profile))
    return profile
  }

  suspend fun signOut() {
    profileDao.clearProfiles()
  }
}

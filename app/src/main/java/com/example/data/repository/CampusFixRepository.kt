package com.example.data.repository

import com.example.data.local.CampusFixDatabase
import com.example.data.local.CampusRoomEntity
import com.example.data.local.FaultReportEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.CampusRoom
import com.example.data.model.FaultReport
import com.example.data.model.MaintenanceTeam
import com.example.data.model.ReportStatus
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class CampusFixRepository(private val database: CampusFixDatabase) {
  private val reportDao = database.faultReportDao()
  private val roomDao = database.campusRoomDao()
  private val profileDao = database.userProfileDao()

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

    // Try extracting code if format is CAMPUSFIX://room/{CODE} or just {CODE}
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
    val cleanName = fullName.trim().ifBlank { if (role == UserRole.WORKER) "UCC Works Technician" else "UCC Student" }
    val cleanEmail = email.trim().ifBlank { if (role == UserRole.WORKER) "works.ucc@gmail.com" else "student@stu.ucc.edu.gh" }
    val cleanDept = department.trim().ifBlank { if (role == UserRole.WORKER) "Works Directorate" else "UCC Campus" }

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

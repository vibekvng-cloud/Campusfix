package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CampusFixDatabase
import com.example.data.model.CampusRoom
import com.example.data.model.FaultCategory
import com.example.data.model.FaultReport
import com.example.data.model.MaintenanceTeam
import com.example.data.model.OnlineWorker
import com.example.data.model.ReportStatus
import com.example.data.model.UrgencyLevel
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.data.repository.CampusFixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavigationTab(val label: String) {
  HOME("Home"),
  MY_REPORTS("My Reports"),
  QUEUE("Job Queue"),
  DASHBOARD("Analytics"),
  PROFILE("Profile")
}

enum class AdminDashboardViewMode(val label: String) {
  STUDENT_VIEW("Student View"),
  STAFF_VIEW("Staff & Ops View")
}

class CampusFixViewModel(application: Application) : AndroidViewModel(application) {
  private val database = CampusFixDatabase.getDatabase(application)
  private val repository = CampusFixRepository(database)

  val allReports: StateFlow<List<FaultReport>> = repository.allReports.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val allRooms: StateFlow<List<CampusRoom>> = repository.allRooms.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val currentProfile: StateFlow<UserProfile?> = repository.currentProfile.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    null
  )

  val onlineWorkers: StateFlow<List<OnlineWorker>> = repository.onlineWorkers.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  private val _adminDashboardViewMode = MutableStateFlow(AdminDashboardViewMode.STAFF_VIEW)
  val adminDashboardViewMode: StateFlow<AdminDashboardViewMode> = _adminDashboardViewMode.asStateFlow()

  private val _activeTab = MutableStateFlow(NavigationTab.HOME)
  val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

  private val _selectedRoomForReport = MutableStateFlow<CampusRoom?>(null)
  val selectedRoomForReport: StateFlow<CampusRoom?> = _selectedRoomForReport.asStateFlow()

  private val _showQrScanDialog = MutableStateFlow(false)
  val showQrScanDialog: StateFlow<Boolean> = _showQrScanDialog.asStateFlow()

  private val _showRoomQrModal = MutableStateFlow<CampusRoom?>(null)
  val showRoomQrModal: StateFlow<CampusRoom?> = _showRoomQrModal.asStateFlow()

  private val _selectedReportDetail = MutableStateFlow<FaultReport?>(null)
  val selectedReportDetail: StateFlow<FaultReport?> = _selectedReportDetail.asStateFlow()

  private val _showAuthDialog = MutableStateFlow(false)
  val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

  private val _reportSuccessMessage = MutableStateFlow<String?>(null)
  val reportSuccessMessage: StateFlow<String?> = _reportSuccessMessage.asStateFlow()

  init {
    viewModelScope.launch {
      CampusFixDatabase.seedDatabase(database)
    }
  }

  fun setTab(tab: NavigationTab) {
    _activeTab.value = tab
  }

  fun setAdminDashboardViewMode(mode: AdminDashboardViewMode) {
    _adminDashboardViewMode.value = mode
  }

  fun openQrScanner() {
    _showQrScanDialog.value = true
  }

  fun closeQrScanner() {
    _showQrScanDialog.value = false
  }

  fun onRoomScanned(room: CampusRoom) {
    _selectedRoomForReport.value = room
    _showQrScanDialog.value = false
  }

  fun clearSelectedRoom() {
    _selectedRoomForReport.value = null
  }

  fun showRoomQr(room: CampusRoom?) {
    _showRoomQrModal.value = room
  }

  fun dismissRoomQrModal() {
    _showRoomQrModal.value = null
  }

  fun showReportDetail(report: FaultReport) {
    _selectedReportDetail.value = report
  }

  fun selectReportDetail(report: FaultReport?) {
    _selectedReportDetail.value = report
  }

  fun dismissReportDetail() {
    _selectedReportDetail.value = null
  }

  fun showAuthModal(show: Boolean) {
    _showAuthDialog.value = show
  }

  fun selectRoomManually(room: CampusRoom) {
    _selectedRoomForReport.value = room
  }

  fun clearSuccessMessage() {
    _reportSuccessMessage.value = null
  }

  fun submitFaultReport(
    room: CampusRoom,
    category: FaultCategory,
    urgency: UrgencyLevel,
    description: String,
    imageAssetRes: Int? = null,
    sampleImageRes: Int? = imageAssetRes,
    onSuccess: () -> Unit = {}
  ) {
    viewModelScope.launch {
      val profile = currentProfile.value
      val reporterId = profile?.id ?: "guest_${UUID.randomUUID().toString().take(6)}"
      val reporterName = profile?.fullName ?: "Campus Visitor"
      val reporterEmail = profile?.email ?: "visitor@ucc.edu.gh"

      val newReport = FaultReport(
        id = "REP-${System.currentTimeMillis().toString().takeLast(6)}",
        roomCode = room.roomCode,
        roomName = room.roomName,
        building = room.building,
        category = category,
        urgency = urgency,
        description = description.trim().ifBlank { "${category.displayName} issue logged at ${room.roomName}" },
        status = ReportStatus.OPEN,
        reporterId = reporterId,
        reporterName = reporterName,
        reporterEmail = reporterEmail,
        sampleImageRes = sampleImageRes ?: imageAssetRes
      )

      repository.submitReport(newReport)
      _selectedRoomForReport.value = null
      _reportSuccessMessage.value = "Report #${newReport.id} logged for ${room.roomName}"
      onSuccess()
    }
  }

  fun assignReportTeam(reportId: String, team: MaintenanceTeam, technician: String? = null) {
    viewModelScope.launch {
      repository.assignTeam(reportId, team, technician)
      _reportSuccessMessage.value = "Assigned to ${team.displayName} Team"
      val current = _selectedReportDetail.value
      if (current?.id == reportId) {
        _selectedReportDetail.value = current.copy(
          assignedTeam = team,
          assignedTechnician = technician ?: team.leadName,
          status = ReportStatus.ASSIGNED
        )
      }
    }
  }

  fun assignTeam(reportId: String, team: MaintenanceTeam, technician: String? = null) {
    assignReportTeam(reportId, team, technician)
  }

  fun assignReportToWorker(reportId: String, worker: OnlineWorker) {
    viewModelScope.launch {
      repository.assignReportToWorker(reportId, worker)
      _reportSuccessMessage.value = "Assigned to ${worker.name} (${worker.team})"
      val current = _selectedReportDetail.value
      if (current?.id == reportId) {
        _selectedReportDetail.value = current.copy(
          assignedTechnician = "${worker.name} (${worker.staffCode})",
          status = ReportStatus.ASSIGNED
        )
      }
    }
  }

  fun updateReportStatus(reportId: String, newStatus: ReportStatus, notes: String? = null) {
    viewModelScope.launch {
      repository.updateReportStatus(reportId, newStatus, notes)
      _reportSuccessMessage.value = "Status updated to ${newStatus.displayName}"
      val current = _selectedReportDetail.value
      if (current != null && current.id == reportId) {
        _selectedReportDetail.value = current.copy(
          status = newStatus,
          resolutionNotes = notes ?: current.resolutionNotes,
          resolvedAtMillis = if (newStatus == ReportStatus.RESOLVED) System.currentTimeMillis() else current.resolvedAtMillis
        )
      }
    }
  }

  fun deleteReport(reportId: String) {
    viewModelScope.launch {
      repository.deleteReport(reportId)
      if (_selectedReportDetail.value?.id == reportId) {
        _selectedReportDetail.value = null
      }
    }
  }

  fun signInOrUpdateProfile(
    studentOrStaffId: String,
    fullName: String,
    email: String,
    role: UserRole,
    department: String
  ) {
    viewModelScope.launch {
      repository.signInOrUpdateProfile(
        studentOrStaffId = studentOrStaffId,
        fullName = fullName,
        email = email,
        role = role,
        department = department
      )
      _showAuthDialog.value = false
    }
  }

  fun signInAsGuest(alias: String) {
    val cleanAlias = alias.trim().ifBlank { "Campus Guest" }
    val guestId = "GUEST/${(1000..9999).random()}"
    signInOrUpdateProfile(
      studentOrStaffId = guestId,
      fullName = cleanAlias,
      email = "${cleanAlias.lowercase().replace(" ", ".")}@guest.ucc.edu.gh",
      role = UserRole.GUEST,
      department = "Campus Guest Privileges"
    )
  }

  fun signOut() {
    viewModelScope.launch {
      repository.signOut()
      _activeTab.value = NavigationTab.HOME
      _selectedRoomForReport.value = null
      _selectedReportDetail.value = null
      _showAuthDialog.value = false
    }
  }
}

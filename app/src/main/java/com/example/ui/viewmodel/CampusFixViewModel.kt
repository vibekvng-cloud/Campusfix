package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CampusFixDatabase
import com.example.data.model.CampusRoom
import com.example.data.model.FaultCategory
import com.example.data.model.FaultReport
import com.example.data.model.MaintenanceTeam
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
      // Ensure seed runs if needed
      CampusFixDatabase.seedDatabase(database)
    }
  }

  fun setTab(tab: NavigationTab) {
    _activeTab.value = tab
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

  fun selectRoomManually(room: CampusRoom) {
    _selectedRoomForReport.value = room
  }

  fun showRoomQr(room: CampusRoom?) {
    _showRoomQrModal.value = room
  }

  fun selectReportDetail(report: FaultReport?) {
    _selectedReportDetail.value = report
  }

  fun showAuthModal(show: Boolean) {
    _showAuthDialog.value = show
  }

  fun clearSuccessMessage() {
    _reportSuccessMessage.value = null
  }

  fun submitFaultReport(
    room: CampusRoom,
    category: FaultCategory,
    urgency: UrgencyLevel,
    description: String,
    photoUri: String? = null,
    sampleImageRes: Int? = null
  ) {
    viewModelScope.launch {
      val user = currentProfile.value
      val report = FaultReport(
        id = "rep_${UUID.randomUUID().toString().take(8)}",
        roomCode = room.roomCode,
        roomName = room.roomName,
        building = room.building,
        category = category,
        urgency = urgency,
        description = description.ifBlank { "${category.displayName} issue reported in ${room.roomName}" },
        status = ReportStatus.OPEN,
        assignedTeam = null,
        reporterId = user?.id ?: "user_ucc_guest",
        reporterName = user?.fullName ?: "UCC Student",
        reporterEmail = user?.email ?: "student@stu.ucc.edu.gh",
        createdAtMillis = System.currentTimeMillis(),
        photoUri = photoUri,
        sampleImageRes = sampleImageRes ?: category.sampleImageRes
      )

      repository.submitReport(report)
      _selectedRoomForReport.value = null
      _reportSuccessMessage.value = "Report logged for ${room.roomCode}! UCC Works & ICT teams notified."
      _activeTab.value = NavigationTab.MY_REPORTS
    }
  }

  fun assignTeam(reportId: String, team: MaintenanceTeam, technician: String?) {
    viewModelScope.launch {
      repository.assignTeam(reportId, team, technician)
      // update selected detail if currently open
      _selectedReportDetail.value?.let { current ->
        if (current.id == reportId) {
          _selectedReportDetail.value = current.copy(
            status = ReportStatus.ASSIGNED,
            assignedTeam = team,
            assignedTechnician = technician ?: team.leadName
          )
        }
      }
    }
  }

  fun updateReportStatus(reportId: String, newStatus: ReportStatus, notes: String? = null) {
    viewModelScope.launch {
      repository.updateReportStatus(reportId, newStatus, notes)
      _selectedReportDetail.value?.let { current ->
        if (current.id == reportId) {
          _selectedReportDetail.value = current.copy(
            status = newStatus,
            resolutionNotes = notes ?: current.resolutionNotes,
            resolvedAtMillis = if (newStatus == ReportStatus.RESOLVED) System.currentTimeMillis() else current.resolvedAtMillis
          )
        }
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

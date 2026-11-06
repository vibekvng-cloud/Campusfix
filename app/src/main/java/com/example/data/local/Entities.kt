package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CampusRoom
import com.example.data.model.FaultCategory
import com.example.data.model.FaultReport
import com.example.data.model.MaintenanceTeam
import com.example.data.model.ReportStatus
import com.example.data.model.UrgencyLevel
import com.example.data.model.UserProfile
import com.example.data.model.UserRole

@Entity(tableName = "fault_reports")
data class FaultReportEntity(
  @PrimaryKey val id: String,
  val roomCode: String,
  val roomName: String,
  val building: String,
  val category: FaultCategory,
  val urgency: UrgencyLevel,
  val description: String,
  val status: ReportStatus,
  val assignedTeam: MaintenanceTeam?,
  val assignedTechnician: String?,
  val reporterId: String,
  val reporterName: String,
  val reporterEmail: String,
  val createdAtMillis: Long,
  val resolvedAtMillis: Long?,
  val resolutionNotes: String?,
  val photoUri: String?,
  val sampleImageRes: Int? = null
) {
  fun toModel(): FaultReport = FaultReport(
    id = id,
    roomCode = roomCode,
    roomName = roomName,
    building = building,
    category = category,
    urgency = urgency,
    description = description,
    status = status,
    assignedTeam = assignedTeam,
    assignedTechnician = assignedTechnician,
    reporterId = reporterId,
    reporterName = reporterName,
    reporterEmail = reporterEmail,
    createdAtMillis = createdAtMillis,
    resolvedAtMillis = resolvedAtMillis,
    resolutionNotes = resolutionNotes,
    photoUri = photoUri,
    sampleImageRes = sampleImageRes ?: category.sampleImageRes
  )

  companion object {
    fun fromModel(model: FaultReport): FaultReportEntity = FaultReportEntity(
      id = model.id,
      roomCode = model.roomCode,
      roomName = model.roomName,
      building = model.building,
      category = model.category,
      urgency = model.urgency,
      description = model.description,
      status = model.status,
      assignedTeam = model.assignedTeam,
      assignedTechnician = model.assignedTechnician,
      reporterId = model.reporterId,
      reporterName = model.reporterName,
      reporterEmail = model.reporterEmail,
      createdAtMillis = model.createdAtMillis,
      resolvedAtMillis = model.resolvedAtMillis,
      resolutionNotes = model.resolutionNotes,
      photoUri = model.photoUri,
      sampleImageRes = model.sampleImageRes ?: model.category.sampleImageRes
    )
  }
}

@Entity(tableName = "campus_rooms")
data class CampusRoomEntity(
  @PrimaryKey val roomCode: String,
  val roomName: String,
  val building: String,
  val floor: String,
  val capacity: Int,
  val commonEquipment: List<String>,
  val qrPayload: String,
  val recurringFaultCount: Int
) {
  fun toModel(): CampusRoom = CampusRoom(
    roomCode = roomCode,
    roomName = roomName,
    building = building,
    floor = floor,
    capacity = capacity,
    commonEquipment = commonEquipment,
    qrPayload = qrPayload,
    recurringFaultCount = recurringFaultCount
  )

  companion object {
    fun fromModel(model: CampusRoom): CampusRoomEntity = CampusRoomEntity(
      roomCode = model.roomCode,
      roomName = model.roomName,
      building = model.building,
      floor = model.floor,
      capacity = model.capacity,
      commonEquipment = model.commonEquipment,
      qrPayload = model.qrPayload,
      recurringFaultCount = model.recurringFaultCount
    )
  }
}

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
  @PrimaryKey val id: String,
  val studentOrStaffId: String,
  val fullName: String,
  val email: String,
  val role: UserRole,
  val department: String,
  val hallOfResidence: String = "Casely Hayford Hall",
  val phone: String,
  val avatarSeed: String
) {
  fun toModel(): UserProfile = UserProfile(
    id = id,
    studentOrStaffId = studentOrStaffId,
    fullName = fullName,
    email = email,
    role = role,
    department = department,
    hallOfResidence = hallOfResidence,
    phone = phone,
    avatarSeed = avatarSeed
  )

  companion object {
    fun fromModel(model: UserProfile): UserProfileEntity = UserProfileEntity(
      id = model.id,
      studentOrStaffId = model.studentOrStaffId,
      fullName = model.fullName,
      email = model.email,
      role = model.role,
      department = model.department,
      hallOfResidence = model.hallOfResidence,
      phone = model.phone,
      avatarSeed = model.avatarSeed
    )
  }
}

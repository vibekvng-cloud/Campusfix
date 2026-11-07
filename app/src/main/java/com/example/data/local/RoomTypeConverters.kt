package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.FaultCategory
import com.example.data.model.MaintenanceTeam
import com.example.data.model.ReportStatus
import com.example.data.model.UrgencyLevel
import com.example.data.model.UserRole

class RoomTypeConverters {

  @TypeConverter
  fun fromFaultCategory(category: FaultCategory): String = category.name

  @TypeConverter
  fun toFaultCategory(value: String): FaultCategory =
    try { FaultCategory.valueOf(value) } catch (e: Exception) { FaultCategory.OTHER }

  @TypeConverter
  fun fromUrgencyLevel(urgency: UrgencyLevel): String = urgency.name

  @TypeConverter
  fun toUrgencyLevel(value: String): UrgencyLevel =
    try { UrgencyLevel.valueOf(value) } catch (e: Exception) { UrgencyLevel.MEDIUM }

  @TypeConverter
  fun fromReportStatus(status: ReportStatus): String = status.name

  @TypeConverter
  fun toReportStatus(value: String): ReportStatus =
    try { ReportStatus.valueOf(value) } catch (e: Exception) { ReportStatus.OPEN }

  @TypeConverter
  fun fromMaintenanceTeam(team: MaintenanceTeam?): String? = team?.name

  @TypeConverter
  fun toMaintenanceTeam(value: String?): MaintenanceTeam? =
    value?.let { try { MaintenanceTeam.valueOf(it) } catch (e: Exception) { null } }

  @TypeConverter
  fun fromUserRole(role: UserRole): String = role.name

  @TypeConverter
  fun toUserRole(value: String): UserRole =
    when (value.uppercase()) {
      "ADMIN" -> UserRole.ADMIN
      "GUEST" -> UserRole.GUEST
      "WORKER", "MAINTENANCE", "STAFF" -> UserRole.WORKER
      else -> UserRole.STUDENT
    }

  @TypeConverter
  fun fromStringList(list: List<String>): String = list.joinToString(";;")

  @TypeConverter
  fun toStringList(value: String?): List<String> =
    if (value.isNullOrBlank()) emptyList() else value.split(";;")
}

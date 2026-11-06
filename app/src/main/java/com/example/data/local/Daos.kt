package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ReportStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface FaultReportDao {
  @Query("SELECT * FROM fault_reports ORDER BY createdAtMillis DESC")
  fun getAllReports(): Flow<List<FaultReportEntity>>

  @Query("SELECT * FROM fault_reports WHERE reporterId = :reporterId ORDER BY createdAtMillis DESC")
  fun getReportsByReporter(reporterId: String): Flow<List<FaultReportEntity>>

  @Query("SELECT * FROM fault_reports WHERE id = :reportId")
  suspend fun getReportById(reportId: String): FaultReportEntity?

  @Query("SELECT * FROM fault_reports WHERE status = :status ORDER BY createdAtMillis DESC")
  fun getReportsByStatus(status: ReportStatus): Flow<List<FaultReportEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReport(report: FaultReportEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReports(reports: List<FaultReportEntity>)

  @Update
  suspend fun updateReport(report: FaultReportEntity)

  @Query("DELETE FROM fault_reports WHERE id = :reportId")
  suspend fun deleteReport(reportId: String)
}

@Dao
interface CampusRoomDao {
  @Query("SELECT * FROM campus_rooms ORDER BY roomName ASC")
  fun getAllRooms(): Flow<List<CampusRoomEntity>>

  @Query("SELECT * FROM campus_rooms WHERE roomCode = :roomCode")
  suspend fun getRoomByCode(roomCode: String): CampusRoomEntity?

  @Query("SELECT * FROM campus_rooms WHERE qrPayload = :payload")
  suspend fun getRoomByQrPayload(payload: String): CampusRoomEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRooms(rooms: List<CampusRoomEntity>)

  @Query("UPDATE campus_rooms SET recurringFaultCount = recurringFaultCount + 1 WHERE roomCode = :roomCode")
  suspend fun incrementFaultCount(roomCode: String)
}

@Dao
interface UserProfileDao {
  @Query("SELECT * FROM user_profiles LIMIT 1")
  fun getCurrentProfile(): Flow<UserProfileEntity?>

  @Query("SELECT * FROM user_profiles WHERE id = :id")
  suspend fun getProfileById(id: String): UserProfileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProfile(profile: UserProfileEntity)

  @Update
  suspend fun updateProfile(profile: UserProfileEntity)

  @Query("DELETE FROM user_profiles")
  suspend fun clearProfiles()
}

package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.R
import com.example.data.model.CampusRoom
import com.example.data.model.FaultCategory
import com.example.data.model.FaultReport
import com.example.data.model.MaintenanceTeam
import com.example.data.model.ReportStatus
import com.example.data.model.UrgencyLevel
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [FaultReportEntity::class, CampusRoomEntity::class, UserProfileEntity::class],
  version = 5,
  exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class CampusFixDatabase : RoomDatabase() {
  abstract fun faultReportDao(): FaultReportDao
  abstract fun campusRoomDao(): CampusRoomDao
  abstract fun userProfileDao(): UserProfileDao

  companion object {
    @Volatile
    private var INSTANCE: CampusFixDatabase? = null

    fun getDatabase(context: Context): CampusFixDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CampusFixDatabase::class.java,
          "campus_fix_db"
        )
          .fallbackToDestructiveMigration()
          .addCallback(DatabaseCallback())
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback : Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          CoroutineScope(Dispatchers.IO).launch {
            seedDatabase(database)
          }
        }
      }
    }

    suspend fun seedDatabase(db: CampusFixDatabase) {
      // NOTE: NO user is pre-signed in. Users must explicitly sign in with their UCC Student ID and School Email!

      // University of Cape Coast (UCC) Lecture Theatres as requested:
      // Sandwich Lecture Hall, NLT, CALC, LT, CODE, plus Casely Hayford
      val uccRooms = listOf(
        CampusRoom(
          roomCode = "SW-LH",
          roomName = "Sandwich Lecture Hall",
          building = "Sandwich Block",
          floor = "Ground Floor",
          capacity = 550,
          commonEquipment = listOf("High-lumen Dual Projectors", "Ceiling Fans", "Audio Mixer & PA", "Tiered Seating"),
          qrPayload = "UCCFIX://room/SW-LH",
          recurringFaultCount = 6
        ),
        CampusRoom(
          roomCode = "NLT",
          roomName = "New Lecture Theatre (NLT)",
          building = "Central Campus / Science Annex",
          floor = "1st Floor",
          capacity = 700,
          commonEquipment = listOf("Epson Ceiling Projector", "Column Speakers", "AC Units 1-4", "Fixed Lecture Benches"),
          qrPayload = "UCCFIX://room/NLT",
          recurringFaultCount = 5
        ),
        CampusRoom(
          roomCode = "CALC",
          roomName = "CALC (Computer Assisted Learning Centre)",
          building = "CALC Complex / North Campus",
          floor = "2nd Floor",
          capacity = 300,
          commonEquipment = listOf("Digital Projector", "Network Workstations", "Split AC Units", "UPS Power Racks"),
          qrPayload = "UCCFIX://room/CALC",
          recurringFaultCount = 3
        ),
        CampusRoom(
          roomCode = "LT",
          roomName = "Main Lecture Theatre (LT)",
          building = "Faculty of Arts / Social Sciences",
          floor = "Ground Floor",
          capacity = 450,
          commonEquipment = listOf("Lecture Podium Projector", "Microphone System", "Auditorium Chairs", "Overhead Lights"),
          qrPayload = "UCCFIX://room/LT",
          recurringFaultCount = 4
        ),
        CampusRoom(
          roomCode = "CODE",
          roomName = "CODE (College of Distance Education Hall)",
          building = "CODE Ultra-Modern Complex",
          floor = "Ground Floor",
          capacity = 600,
          commonEquipment = listOf("Interactive Projector", "Wall Mounted Speakers", "Central AC", "Whiteboard Units"),
          qrPayload = "UCCFIX://room/CODE",
          recurringFaultCount = 2
        ),
        CampusRoom(
          roomCode = "CH-HALL",
          roomName = "Casely Hayford Hall Facility",
          building = "Casely Hayford Hall Area",
          floor = "Ground Floor",
          capacity = 350,
          commonEquipment = listOf("Ceiling Fans", "Fluorescent Fixtures", "Washroom Sinks & Plumbing"),
          qrPayload = "UCCFIX://room/CH-HALL",
          recurringFaultCount = 4
        )
      )
      db.campusRoomDao().insertRooms(uccRooms.map { CampusRoomEntity.fromModel(it) })

      // Seed initial realistic UCC fault reports with images
      val now = System.currentTimeMillis()
      val hour = 3600_000L

      val initialUccReports = listOf(
        FaultReport(
          id = "ucc_rep_001",
          roomCode = "SW-LH",
          roomName = "Sandwich Lecture Hall",
          building = "Sandwich Block",
          category = FaultCategory.PROJECTOR,
          urgency = UrgencyLevel.HIGH,
          description = "Ceiling projector hanging with loose HDMI cables and showing 'NO SIGNAL'. Level 300 class starts soon.",
          status = ReportStatus.OPEN,
          assignedTeam = null,
          reporterId = "PS/CSC/21/0018",
          reporterName = "Emmanuel Tetteh",
          reporterEmail = "emmanuel.tetteh@stu.ucc.edu.gh",
          createdAtMillis = now - (20 * 60_000L),
          sampleImageRes = R.drawable.fault_spoilt_projector_1793844723958
        ),
        FaultReport(
          id = "ucc_rep_002",
          roomCode = "LT",
          roomName = "Main Lecture Theatre (LT)",
          building = "Faculty of Arts / Social Sciences",
          category = FaultCategory.FURNITURE,
          urgency = UrgencyLevel.MEDIUM,
          description = "Wooden student chairs in Row D and E have cracked armrests and missing screws. Dangerous for students sitting.",
          status = ReportStatus.ASSIGNED,
          assignedTeam = MaintenanceTeam.CARPENTRY,
          assignedTechnician = "Kwesi Arthur",
          reporterId = "ED/EPS/22/0084",
          reporterName = "Gifty Osei",
          reporterEmail = "gifty.osei@stu.ucc.edu.gh",
          createdAtMillis = now - (50 * 60_000L),
          sampleImageRes = R.drawable.fault_broken_chair_1793844710427
        ),
        FaultReport(
          id = "ucc_rep_003",
          roomCode = "NLT",
          roomName = "New Lecture Theatre (NLT)",
          building = "Central Campus / Science Annex",
          category = FaultCategory.LIGHTING,
          urgency = UrgencyLevel.HIGH,
          description = "Fluorescent light tubes buzzing and flickering near the podium; ceiling panel sagging.",
          status = ReportStatus.FIXING,
          assignedTeam = MaintenanceTeam.ELECTRICAL,
          assignedTechnician = "Ing. Emmanuel Addo",
          reporterId = "STAFF/0411",
          reporterName = "Dr. Kofi Boateng",
          reporterEmail = "k.boateng@ucc.edu.gh",
          createdAtMillis = now - (2 * hour),
          sampleImageRes = R.drawable.fault_broken_light_1793844737319
        ),
        FaultReport(
          id = "ucc_rep_004",
          roomCode = "CALC",
          roomName = "CALC (Computer Assisted Learning Centre)",
          building = "CALC Complex / North Campus",
          category = FaultCategory.PLUMBING,
          urgency = UrgencyLevel.MEDIUM,
          description = "Ground floor washroom tap broken and water gushing continuously across floor tiles.",
          status = ReportStatus.RESOLVED,
          assignedTeam = MaintenanceTeam.PLUMBING,
          assignedTechnician = "Kofi Mensah",
          reporterId = "ET/ICT/20/0112",
          reporterName = "Francis Baah",
          reporterEmail = "francis.baah@stu.ucc.edu.gh",
          createdAtMillis = now - (6 * hour),
          resolvedAtMillis = now - (1 * hour),
          resolutionNotes = "Replaced cracked copper bibcock tap and cleared drain pipe blockage.",
          sampleImageRes = R.drawable.fault_leaking_tap_1793844751100
        ),
        FaultReport(
          id = "ucc_rep_005",
          roomCode = "CODE",
          roomName = "CODE (College of Distance Education Hall)",
          building = "CODE Ultra-Modern Complex",
          category = FaultCategory.AIR_CONDITIONING,
          urgency = UrgencyLevel.HIGH,
          description = "Ceiling AC unit grille dropped loose with clogged filter and making loud rattling noise during lecture.",
          status = ReportStatus.OPEN,
          assignedTeam = MaintenanceTeam.ELECTRICAL,
          assignedTechnician = "Ing. Emmanuel Addo",
          reporterId = "ED/EPS/22/0119",
          reporterName = "Abena Asante",
          reporterEmail = "abena.asante@stu.ucc.edu.gh",
          createdAtMillis = now - (35 * 60_000L),
          sampleImageRes = R.drawable.fault_ventilation_ac_1793925507876
        )
      )
      db.faultReportDao().insertReports(initialUccReports.map { FaultReportEntity.fromModel(it) })
    }
  }
}

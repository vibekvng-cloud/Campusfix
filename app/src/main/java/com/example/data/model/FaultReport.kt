package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.R
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityHighBg
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityLowBg
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.PriorityMediumBg
import com.example.ui.theme.StatusAssignedBg
import com.example.ui.theme.StatusAssignedNeon
import com.example.ui.theme.StatusFixingBg
import com.example.ui.theme.StatusFixingNeon
import com.example.ui.theme.StatusOpenBg
import com.example.ui.theme.StatusOpenNeon
import com.example.ui.theme.StatusResolvedBg
import com.example.ui.theme.StatusResolvedNeon

enum class FaultCategory(
  val displayName: String,
  val icon: ImageVector,
  val accentColor: Color,
  val sampleImageRes: Int?
) {
  PROJECTOR("Spoilt Projector & AV", Icons.Filled.Tv, NeonMagenta, R.drawable.fault_spoilt_projector_1793844723958),
  FURNITURE("Broken Chairs & Desks", Icons.Filled.Chair, NeonAmber, R.drawable.fault_broken_chair_1793844710427),
  LIGHTING("Broken Lights & Sockets", Icons.Filled.Lightbulb, NeonCyan, R.drawable.fault_broken_light_1793844737319),
  PLUMBING("Plumbing & Washrooms", Icons.Filled.Plumbing, NeonPink, R.drawable.fault_leaking_tap_1793844751100),
  AIR_CONDITIONING("Fans & Air Conditioning", Icons.Filled.AcUnit, NeonPurple, R.drawable.fault_ventilation_ac_1793925507876),
  OTHER("General Maintenance", Icons.Filled.Warning, NeonIndigo, null)
}

enum class UrgencyLevel(val displayName: String, val textColor: Color, val bgColor: Color) {
  LOW("Low", PriorityLow, PriorityLowBg),
  MEDIUM("Medium", PriorityMedium, PriorityMediumBg),
  HIGH("Urgent / Class Starting", PriorityHigh, PriorityHighBg)
}

enum class ReportStatus(val displayName: String, val stepIndex: Int, val textColor: Color, val bgColor: Color) {
  OPEN("Open", 0, StatusOpenNeon, StatusOpenBg),
  ASSIGNED("Assigned", 1, StatusAssignedNeon, StatusAssignedBg),
  FIXING("Fixing", 2, StatusFixingNeon, StatusFixingBg),
  RESOLVED("Resolved", 3, StatusResolvedNeon, StatusResolvedBg)
}

enum class MaintenanceTeam(val displayName: String, val code: String, val leadName: String) {
  ELECTRICAL("UCC Directorate of Physical Dev. (Electrical)", "ELEC", "Ing. Emmanuel Addo"),
  PLUMBING("UCC Works & Physical Plant (Plumbing)", "PLUMB", "Kofi Mensah"),
  IT_AV("UCC ICT Directorate (Audio-Visual Services)", "ICT-AV", "Grace Koomson"),
  CARPENTRY("UCC Estate & Carpentry Section", "CARP", "Kwesi Arthur")
}

data class FaultReport(
  val id: String,
  val roomCode: String,
  val roomName: String,
  val building: String,
  val category: FaultCategory,
  val urgency: UrgencyLevel,
  val description: String,
  val status: ReportStatus,
  val assignedTeam: MaintenanceTeam? = null,
  val assignedTechnician: String? = null,
  val reporterId: String,
  val reporterName: String,
  val reporterEmail: String,
  val createdAtMillis: Long = System.currentTimeMillis(),
  val resolvedAtMillis: Long? = null,
  val resolutionNotes: String? = null,
  val photoUri: String? = null,
  val sampleImageRes: Int? = null
)

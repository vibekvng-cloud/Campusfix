package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.OutlinedButton
import com.example.data.model.OnlineWorker
import com.example.ui.viewmodel.AdminDashboardViewMode
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CampusRoom
import com.example.data.model.FaultCategory
import com.example.data.model.FaultReport
import com.example.data.model.MaintenanceTeam
import com.example.data.model.ReportStatus
import com.example.data.model.UserRole
import com.example.ui.components.BeginnerVisualProblemCard
import com.example.ui.components.FaultOverviewDonutCard
import com.example.ui.components.FaultReportCard
import com.example.ui.components.QuickActionNeonPill
import com.example.ui.components.SkillAlley3DHeroCard
import com.example.ui.components.SkillAlleyDarkProcessCard
import com.example.ui.components.StatusProgressStepper
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SkillAlleyBg
import com.example.ui.theme.SkillAlleyBorder
import com.example.ui.theme.SkillAlleyButtonDark
import com.example.ui.theme.SkillAlleyCard
import com.example.ui.theme.SkillAlleyDarkCard
import com.example.ui.theme.SkillAlleyTextMuted
import com.example.ui.theme.SkillAlleyTextPrimary
import com.example.ui.theme.SkillAlleyTextSecondary
import com.example.ui.theme.UccGold
import com.example.ui.viewmodel.CampusFixViewModel
import com.example.ui.viewmodel.NavigationTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
  viewModel: CampusFixViewModel,
  onNavigateToReport: (preselectedCategory: FaultCategory?, preselectedImage: Int?) -> Unit,
  onViewAllReports: () -> Unit,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.currentProfile.collectAsState()
  val adminMode by viewModel.adminDashboardViewMode.collectAsState()

  when (profile?.role) {
    UserRole.ADMIN -> {
      Column(
        modifier = modifier
          .fillMaxSize()
          .background(SkillAlleyBg)
      ) {
        // Mode Switcher Header for Admin (Toggle between Staff/Ops Desk & Student Campus View)
        AdminModeSwitcherHeader(
          activeMode = adminMode,
          onModeChange = { viewModel.setAdminDashboardViewMode(it) }
        )

        when (adminMode) {
          AdminDashboardViewMode.STAFF_VIEW -> {
            WorkerDashboardView(
              viewModel = viewModel,
              onViewAllReports = onViewAllReports,
              isAdmin = true,
              modifier = Modifier.weight(1f)
            )
          }
          AdminDashboardViewMode.STUDENT_VIEW -> {
            StudentDashboardView(
              viewModel = viewModel,
              onNavigateToReport = onNavigateToReport,
              onViewAllReports = onViewAllReports,
              isGuest = false,
              isAdminPreview = true,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }
    UserRole.WORKER -> {
      WorkerDashboardView(
        viewModel = viewModel,
        onViewAllReports = onViewAllReports,
        isAdmin = false,
        modifier = modifier
      )
    }
    UserRole.GUEST -> {
      StudentDashboardView(
        viewModel = viewModel,
        onNavigateToReport = onNavigateToReport,
        onViewAllReports = onViewAllReports,
        isGuest = true,
        isAdminPreview = false,
        modifier = modifier
      )
    }
    UserRole.STUDENT, null -> {
      StudentDashboardView(
        viewModel = viewModel,
        onNavigateToReport = onNavigateToReport,
        onViewAllReports = onViewAllReports,
        isGuest = false,
        isAdminPreview = false,
        modifier = modifier
      )
    }
  }
}

@Composable
fun AdminModeSwitcherHeader(
  activeMode: AdminDashboardViewMode,
  onModeChange: (AdminDashboardViewMode) -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = SkillAlleyCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
  ) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(Color(0xFFF3E8FF)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = Color(0xFF7E22CE), modifier = Modifier.size(15.dp))
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "DIRECTORATE ADMIN CONTROL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary,
            letterSpacing = 0.5.sp
          )
        }

        Surface(
          color = SkillAlleyButtonDark,
          shape = RoundedCornerShape(100.dp)
        ) {
          Text(
            text = "Full Access",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Segmented Mode Switcher
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(SkillAlleyBg, RoundedCornerShape(10.dp))
          .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        val isStaff = activeMode == AdminDashboardViewMode.STAFF_VIEW
        Surface(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onModeChange(AdminDashboardViewMode.STAFF_VIEW) }
            .testTag("admin_toggle_staff_view"),
          shape = RoundedCornerShape(8.dp),
          color = if (isStaff) SkillAlleyButtonDark else Color.Transparent
        ) {
          Row(
            modifier = Modifier.padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(Icons.Filled.Engineering, contentDescription = null, tint = if (isStaff) Color.White else SkillAlleyTextSecondary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Staff & Works Desk",
              fontSize = 11.sp,
              fontWeight = if (isStaff) FontWeight.Bold else FontWeight.Medium,
              color = if (isStaff) Color.White else SkillAlleyTextSecondary
            )
          }
        }

        val isStudent = activeMode == AdminDashboardViewMode.STUDENT_VIEW
        Surface(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onModeChange(AdminDashboardViewMode.STUDENT_VIEW) }
            .testTag("admin_toggle_student_view"),
          shape = RoundedCornerShape(8.dp),
          color = if (isStudent) SkillAlleyButtonDark else Color.Transparent
        ) {
          Row(
            modifier = Modifier.padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(Icons.Filled.School, contentDescription = null, tint = if (isStudent) Color.White else SkillAlleyTextSecondary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Student Campus View",
              fontSize = 11.sp,
              fontWeight = if (isStudent) FontWeight.Bold else FontWeight.Medium,
              color = if (isStudent) Color.White else SkillAlleyTextSecondary
            )
          }
        }
      }
    }
  }
}

/**
 * -------------------------------------------------------------
 * STUDENT DASHBOARD VIEW
 * -------------------------------------------------------------
 * Tailored for students & campus guests: 3D model diagnostics, 1-tap QR scanning,
 * visual problem photos, and live fix tracking.
 */
@Composable
fun StudentDashboardView(
  viewModel: CampusFixViewModel,
  onNavigateToReport: (preselectedCategory: FaultCategory?, preselectedImage: Int?) -> Unit,
  onViewAllReports: () -> Unit,
  isGuest: Boolean = false,
  isAdminPreview: Boolean = false,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.currentProfile.collectAsState()
  val reports by viewModel.allReports.collectAsState()
  val rooms by viewModel.allRooms.collectAsState()

  val activeReports = reports.filter { it.status != ReportStatus.RESOLVED }
  val resolvedReports = reports.filter { it.status == ReportStatus.RESOLVED }

  // Student's personal reports
  val myReports = reports.filter { it.reporterId == profile?.id }
  val myLatestActiveReport = myReports.firstOrNull { it.status != ReportStatus.RESOLVED }

  val categoryCounts = FaultCategory.entries.associateWith { cat ->
    reports.count { it.category == cat }
  }

  val firstName = profile?.fullName?.split(" ")?.firstOrNull() ?: if (isGuest) "Guest" else "Student"

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(SkillAlleyBg)
      .testTag("student_dashboard_screen"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    if (isGuest) {
      item {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFFECFDF5),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
          modifier = Modifier.fillMaxWidth().testTag("guest_mode_banner")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Campus Guest Privileges Active", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
              Text("You can inspect lecture halls, explore issues, and report campus faults with your guest alias.", fontSize = 11.sp, color = Color(0xFF047857))
            }
          }
        }
      }
    }

    if (isAdminPreview) {
      item {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFFF3E8FF),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD8B4FE)),
          modifier = Modifier.fillMaxWidth().testTag("admin_preview_banner")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = Color(0xFF7E22CE), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Administrator Preview Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF581C87))
              Text("Viewing student dashboard live as seen by UCC students.", fontSize = 11.sp, color = Color(0xFF7E22CE))
            }
          }
        }
      }
    }
    // Top Bar with SkillAlley Greeting & Student Identity
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Good Day, ",
              fontSize = 14.sp,
              color = SkillAlleyTextSecondary
            )
            Text(
              text = "$firstName 👋",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = SkillAlleyTextPrimary
            )
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = profile?.studentOrStaffId ?: "UCC Student",
              fontSize = 12.sp,
              color = SkillAlleyButtonDark,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = " • UCC Ghana",
              fontSize = 11.sp,
              color = SkillAlleyTextMuted,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Notification with badge
          BadgedBox(
            badge = {
              if (activeReports.isNotEmpty()) {
                Badge(
                  containerColor = SkillAlleyButtonDark,
                  contentColor = Color.White
                ) {
                  Text(text = "${activeReports.size.coerceAtMost(9)}")
                }
              }
            }
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(SkillAlleyCard)
                .border(1.dp, SkillAlleyBorder, CircleShape)
                .clickable { onViewAllReports() },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notifications",
                tint = SkillAlleyTextPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          // Avatar Pill
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(SkillAlleyButtonDark)
              .clickable { viewModel.setTab(NavigationTab.PROFILE) },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = firstName.take(1).uppercase(),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
          }
        }
      }
    }

    // Featured Skillalley 3D Interactive Fault Diagnostics
    item {
      SkillAlley3DHeroCard(
        totalActive = activeReports.size,
        totalResolved = resolvedReports.size,
        onReportClick = { selectedCat ->
          val targetRoom = when (selectedCat) {
            FaultCategory.PROJECTOR -> rooms.find { it.roomCode == "SW-LH" }
            FaultCategory.FURNITURE -> rooms.find { it.roomCode == "LT" }
            FaultCategory.AIR_CONDITIONING -> rooms.find { it.roomCode == "CODE" }
            FaultCategory.PLUMBING -> rooms.find { it.roomCode == "CALC" }
            FaultCategory.LIGHTING -> rooms.find { it.roomCode == "NLT" }
            else -> rooms.firstOrNull()
          } ?: rooms.firstOrNull()
          if (targetRoom != null) viewModel.selectRoomManually(targetRoom)
          onNavigateToReport(selectedCat, selectedCat.sampleImageRes)
        },
        onScanClick = { viewModel.openQrScanner() }
      )
    }

    // Inset Dark Floating Process Card
    item {
      SkillAlleyDarkProcessCard(
        onViewQueue = { onViewAllReports() }
      )
    }

    // Live Report Tracking Card (If student has an active report)
    if (myLatestActiveReport != null) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("student_live_report_tracker"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  color = Color(0xFF10B981).copy(alpha = 0.15f),
                  shape = CircleShape,
                  modifier = Modifier.size(10.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .fillMaxSize()
                      .background(Color(0xFF10B981), CircleShape)
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "MY LIVE FAULT TRACKER",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = SkillAlleyButtonDark,
                  letterSpacing = 0.6.sp
                )
              }

              Surface(
                color = myLatestActiveReport.status.bgColor,
                shape = RoundedCornerShape(100.dp)
              ) {
                Text(
                  text = myLatestActiveReport.status.displayName,
                  color = myLatestActiveReport.status.textColor,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "${myLatestActiveReport.category.displayName} in ${myLatestActiveReport.roomName}",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = SkillAlleyTextPrimary
            )
            Text(
              text = if (myLatestActiveReport.assignedTeam != null) {
                "Assigned to ${myLatestActiveReport.assignedTeam?.displayName} • Team on site"
              } else {
                "Waiting for Works Department dispatch assignment"
              },
              fontSize = 11.sp,
              color = SkillAlleyTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatusProgressStepper(
              currentStatus = myLatestActiveReport.status,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }

    // Beginner-Friendly Visual Problem Cards (Tap a Photo to Report)
    item {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Visual Fault Picker",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = SkillAlleyTextPrimary
            )
            Text(
              text = "Tap a photo of what is broken to report fast",
              fontSize = 11.sp,
              color = SkillAlleyTextSecondary
            )
          }

          Surface(
            color = SkillAlleyButtonDark,
            shape = RoundedCornerShape(100.dp)
          ) {
            Text(
              text = "Beginner Easy",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          item {
            BeginnerVisualProblemCard(
              title = "Broken Chairs & Desks",
              subtitle = "Cracked armrests / missing screws",
              imageRes = R.drawable.fault_broken_chair_1793844710427,
              accentColor = NeonAmber,
              onClick = {
                val defaultRoom = rooms.find { it.roomCode == "LT" } ?: rooms.firstOrNull()
                if (defaultRoom != null) viewModel.selectRoomManually(defaultRoom)
                onNavigateToReport(FaultCategory.FURNITURE, R.drawable.fault_broken_chair_1793844710427)
              }
            )
          }

          item {
            BeginnerVisualProblemCard(
              title = "Spoilt Projector & Cables",
              subtitle = "No signal / hanging HDMI cables",
              imageRes = R.drawable.fault_spoilt_projector_1793844723958,
              accentColor = NeonMagenta,
              onClick = {
                val defaultRoom = rooms.find { it.roomCode == "SW-LH" } ?: rooms.firstOrNull()
                if (defaultRoom != null) viewModel.selectRoomManually(defaultRoom)
                onNavigateToReport(FaultCategory.PROJECTOR, R.drawable.fault_spoilt_projector_1793844723958)
              }
            )
          }

          item {
            BeginnerVisualProblemCard(
              title = "Poor Ventilation & AC",
              subtitle = "Dropped filter grille / noisy fans",
              imageRes = R.drawable.fault_ventilation_ac_1793925507876,
              accentColor = NeonPurple,
              onClick = {
                val defaultRoom = rooms.find { it.roomCode == "CODE" } ?: rooms.firstOrNull()
                if (defaultRoom != null) viewModel.selectRoomManually(defaultRoom)
                onNavigateToReport(FaultCategory.AIR_CONDITIONING, R.drawable.fault_ventilation_ac_1793925507876)
              }
            )
          }

          item {
            BeginnerVisualProblemCard(
              title = "Flickering Lights / Sockets",
              subtitle = "Dark spots & buzzing tubes",
              imageRes = R.drawable.fault_broken_light_1793844737319,
              accentColor = NeonCyan,
              onClick = {
                val defaultRoom = rooms.find { it.roomCode == "NLT" } ?: rooms.firstOrNull()
                if (defaultRoom != null) viewModel.selectRoomManually(defaultRoom)
                onNavigateToReport(FaultCategory.LIGHTING, R.drawable.fault_broken_light_1793844737319)
              }
            )
          }

          item {
            BeginnerVisualProblemCard(
              title = "Leaking Washroom Taps",
              subtitle = "Water gushing / drain blockage",
              imageRes = R.drawable.fault_leaking_tap_1793844751100,
              accentColor = NeonPink,
              onClick = {
                val defaultRoom = rooms.find { it.roomCode == "CALC" } ?: rooms.firstOrNull()
                if (defaultRoom != null) viewModel.selectRoomManually(defaultRoom)
                onNavigateToReport(FaultCategory.PLUMBING, R.drawable.fault_leaking_tap_1793844751100)
              }
            )
          }
        }
      }
    }

    // Quick Action Pills
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        QuickActionNeonPill(
          icon = Icons.Filled.QrCodeScanner,
          label = "Scan QR",
          onClick = { viewModel.openQrScanner() }
        )
        QuickActionNeonPill(
          icon = Icons.Filled.Add,
          label = "Quick Report",
          onClick = {
            val firstRoom = rooms.firstOrNull()
            if (firstRoom != null) viewModel.selectRoomManually(firstRoom)
            onNavigateToReport(null, null)
          }
        )
        QuickActionNeonPill(
          icon = Icons.Filled.Assignment,
          label = "My Reports",
          badgeText = if (activeReports.isNotEmpty()) "${activeReports.size}" else null,
          onClick = { onViewAllReports() }
        )
        QuickActionNeonPill(
          icon = Icons.Filled.MeetingRoom,
          label = "UCC Theatres",
          onClick = {
            val firstRoom = rooms.firstOrNull()
            if (firstRoom != null) viewModel.showRoomQr(firstRoom)
          }
        )
      }
    }

    // Donut Breakdown Card
    item {
      FaultOverviewDonutCard(
        categoryCounts = categoryCounts,
        totalCount = reports.size
      )
    }

    // Recent UCC Reports Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Recent UCC Fault Reports",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )
          Text(
            text = "Track repairs in Sandwich, NLT, CALC, LT, CODE",
            fontSize = 11.sp,
            color = SkillAlleyTextSecondary
          )
        }

        Text(
          text = "See All",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = SkillAlleyButtonDark,
          modifier = Modifier
            .clickable { onViewAllReports() }
            .testTag("see_all_reports_button")
        )
      }
    }

    val recentList = reports.take(4)
    if (recentList.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "No fault reports logged yet",
              fontWeight = FontWeight.Medium,
              color = SkillAlleyTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Scan a lecture theatre QR code to report",
              fontSize = 11.sp,
              color = SkillAlleyTextMuted
            )
          }
        }
      }
    } else {
      items(recentList, key = { it.id }) { report ->
        FaultReportCard(
          report = report,
          onClick = { viewModel.selectReportDetail(report) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

/**
 * -------------------------------------------------------------
 * WORKER DASHBOARD VIEW (UCC Works Department Operations Desk)
 * -------------------------------------------------------------
 * Designed for UCC Works Dept engineers, technicians & artisans.
 * Features:
 *  - LIVE STUDENT REPORT ALERT BANNER: Real-time notification of newly filed student faults.
 *  - Operations metrics (Open, Fixing, Resolved Today, Avg Repair Time with '85' metric badge).
 *  - Trade team filters (Electrical, Plumbing, IT & AV, Carpentry).
 *  - 1-tap rapid dispatch and status actions.
 *  - Hotspot lecture theatres tracking.
 */
@Composable
fun WorkerDashboardView(
  viewModel: CampusFixViewModel,
  onViewAllReports: () -> Unit,
  isAdmin: Boolean = false,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.currentProfile.collectAsState()
  val reports by viewModel.allReports.collectAsState()
  val rooms by viewModel.allRooms.collectAsState()
  val onlineWorkers by viewModel.onlineWorkers.collectAsState()

  var selectedTradeFilter by remember { mutableStateOf<MaintenanceTeam?>(null) }
  var reportForWorkerAssignment by remember { mutableStateOf<FaultReport?>(null) }

  // Filter reports
  val openReports = reports.filter { it.status == ReportStatus.OPEN }
  val fixingReports = reports.filter { it.status == ReportStatus.FIXING }
  val resolvedReports = reports.filter { it.status == ReportStatus.RESOLVED }

  // Most recent student report that is open or unassigned (LIVE ALERT)
  val latestStudentAlert = reports.firstOrNull { it.status == ReportStatus.OPEN }

  val filteredQueue = reports.filter { report ->
    val matchesTrade = selectedTradeFilter == null || report.assignedTeam == selectedTradeFilter
    matchesTrade && report.status != ReportStatus.RESOLVED
  }

  // Animation for live alert pulse
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(800),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  // Admin Worker Assignment Dialog
  if (reportForWorkerAssignment != null) {
    AdminAssignWorkerDialog(
      report = reportForWorkerAssignment!!,
      onlineWorkers = onlineWorkers,
      onAssign = { worker ->
        viewModel.assignReportToWorker(reportForWorkerAssignment!!.id, worker)
        reportForWorkerAssignment = null
      },
      onDismiss = { reportForWorkerAssignment = null }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(SkillAlleyBg)
      .testTag("worker_dashboard_screen"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Operations Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = SkillAlleyButtonDark,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = if (isAdmin) "DIRECTORATE ADMIN" else "UCC WORKS DEPT",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isAdmin) "Master Dispatch Desk" else "Operations Desk",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextSecondary
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = if (isAdmin) (profile?.fullName ?: "Prof. K. Baidoo (Admin)") else (profile?.fullName ?: "UCC Maintenance Engineer"),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )

          Text(
            text = if (isAdmin) "Physical Development & Works Directorate • ADMIN/001" else "${profile?.department ?: "Works Directorate"} • ${profile?.studentOrStaffId ?: "STAFF/0891"}",
            fontSize = 12.sp,
            color = SkillAlleyTextMuted
          )
        }

        // Live status pill
        Surface(
          color = (if (isAdmin) Color(0xFF6366F1) else Color(0xFF10B981)).copy(alpha = 0.12f),
          shape = RoundedCornerShape(100.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, (if (isAdmin) Color(0xFF6366F1) else Color(0xFF10B981)).copy(alpha = 0.3f))
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .scale(pulseScale)
                .background(if (isAdmin) Color(0xFF6366F1) else Color(0xFF10B981), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isAdmin) "Full Control" else "On Duty",
              color = if (isAdmin) Color(0xFF4338CA) else Color(0xFF047857),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // 🚨 LIVE STUDENT FAULT ALERT BANNER (CRITICAL USER REQUEST)
    if (latestStudentAlert != null) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("live_student_fault_alert"),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = SkillAlleyDarkCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(10.dp)
                    .scale(pulseScale)
                    .background(Color(0xFFEF4444), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "🚨 LIVE STUDENT FAULT ALERT",
                  color = Color(0xFFFCA5A5),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.8.sp
                )
              }

              Surface(
                color = Color(0xFF334155),
                shape = RoundedCornerShape(100.dp)
              ) {
                Text(
                  text = "Immediate Action",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "${latestStudentAlert.category.displayName} in ${latestStudentAlert.roomName} (${latestStudentAlert.roomCode})",
              color = Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "\"${latestStudentAlert.description}\"",
              color = Color(0xFFCBD5E1),
              fontSize = 12.sp,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Reported by: ${latestStudentAlert.reporterName}",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
              )
              Text(
                text = " • Urgency: ${latestStudentAlert.urgency.displayName}",
                color = latestStudentAlert.urgency.textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              if (isAdmin) {
                Button(
                  onClick = { reportForWorkerAssignment = latestStudentAlert },
                  modifier = Modifier
                    .weight(1f)
                    .testTag("admin_dispatch_alert_btn"),
                  colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                  shape = RoundedCornerShape(100.dp)
                ) {
                  Text(
                    text = "Assign Online Worker ->",
                    color = SkillAlleyButtonDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              } else {
                Button(
                  onClick = {
                    val assignedTeam = when (latestStudentAlert.category) {
                      FaultCategory.PROJECTOR -> MaintenanceTeam.IT_AV
                      FaultCategory.LIGHTING -> MaintenanceTeam.ELECTRICAL
                      FaultCategory.PLUMBING -> MaintenanceTeam.PLUMBING
                      FaultCategory.FURNITURE -> MaintenanceTeam.CARPENTRY
                      FaultCategory.AIR_CONDITIONING -> MaintenanceTeam.ELECTRICAL
                      else -> MaintenanceTeam.ELECTRICAL
                    }
                    viewModel.assignTeam(latestStudentAlert.id, assignedTeam, profile?.fullName)
                  },
                  modifier = Modifier
                    .weight(1f)
                    .testTag("quick_dispatch_alert_btn"),
                  colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                  shape = RoundedCornerShape(100.dp)
                ) {
                  Text(
                    text = "Pick & Dispatch ->",
                    color = SkillAlleyButtonDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(100.dp))
                  .clickable { viewModel.selectReportDetail(latestStudentAlert) },
                color = Color(0xFF272A34),
                shape = RoundedCornerShape(100.dp)
              ) {
                Text(
                  text = "Open Sheet",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
              }
            }
          }
        }
      }
    }

    // WORKS PERSONNEL ON DUTY ROSTER (CRITICAL ADMIN CAPABILITY)
    if (isAdmin) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_online_workers_card"),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Works Personnel On Duty",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkillAlleyTextPrimary
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .background(Color(0xFF10B981), CircleShape)
                  )
                }
                Text(
                  text = "${onlineWorkers.count { it.isOnline }} Active UCC Technicians Ready for Dispatch",
                  fontSize = 11.sp,
                  color = SkillAlleyTextSecondary
                )
              }

              Surface(
                color = SkillAlleyBg,
                shape = RoundedCornerShape(100.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
              ) {
                Text(
                  text = "Live Roster",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = SkillAlleyTextPrimary,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              items(onlineWorkers) { worker ->
                OnlineWorkerRosterCard(worker = worker)
              }
            }
          }
        }
      }
    }

    // Operations Metric Cards (Matching SkillAlley Metric Badges 75, 85)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        WorkerMetricCard(
          title = "Open Jobs",
          value = "${openReports.size}",
          subtitle = "Awaiting dispatch",
          badgeText = null,
          modifier = Modifier.weight(1f)
        )
        WorkerMetricCard(
          title = "Fixing Now",
          value = "${fixingReports.size}",
          subtitle = "Teams on site",
          badgeText = "LIVE",
          modifier = Modifier.weight(1f)
        )
        WorkerMetricCard(
          title = "Fixed Today",
          value = "${resolvedReports.size}",
          subtitle = "Avg 38 mins",
          badgeText = "85",
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Trade Section Filter Pills (SkillAlley Pill Style)
    item {
      Column {
        Text(
          text = "Filter by Trade Unit:",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = SkillAlleyTextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          item {
            val isSelected = selectedTradeFilter == null
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .clickable { selectedTradeFilter = null },
              shape = RoundedCornerShape(100.dp),
              color = if (isSelected) SkillAlleyButtonDark else SkillAlleyCard,
              border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
            ) {
              Text(
                text = "All Trades (${reports.size})",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else SkillAlleyTextPrimary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
              )
            }
          }

          items(MaintenanceTeam.entries) { team ->
            val isSelected = selectedTradeFilter == team
            val count = reports.count { it.assignedTeam == team }
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .clickable { selectedTradeFilter = team },
              shape = RoundedCornerShape(100.dp),
              color = if (isSelected) SkillAlleyButtonDark else SkillAlleyCard,
              border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
            ) {
              Text(
                text = "${team.displayName} ($count)",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else SkillAlleyTextPrimary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
              )
            }
          }
        }
      }
    }

    // Dispatch Queue Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Active Job Queue",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )
          Text(
            text = "Rapid action for Sandwich, NLT, CALC, LT & CODE",
            fontSize = 11.sp,
            color = SkillAlleyTextSecondary
          )
        }

        Surface(
          color = SkillAlleyCard,
          shape = RoundedCornerShape(100.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
          modifier = Modifier.clickable { onViewAllReports() }
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "Full List",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyButtonDark
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = SkillAlleyButtonDark,
              modifier = Modifier.size(12.dp)
            )
          }
        }
      }
    }

    // Queue Items with Rapid Status Actions
    if (filteredQueue.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Filled.CheckCircle,
              contentDescription = null,
              tint = Color(0xFF10B981),
              modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No pending faults in this trade unit",
              fontWeight = FontWeight.Bold,
              color = SkillAlleyTextPrimary
            )
            Text(
              text = "All classrooms running smoothly",
              fontSize = 11.sp,
              color = SkillAlleyTextSecondary
            )
          }
        }
      }
    } else {
      items(filteredQueue, key = { it.id }) { report ->
        WorkerJobDispatchCard(
          report = report,
          isAdmin = isAdmin,
          onOpenDetail = { viewModel.selectReportDetail(report) },
          onAssignToMe = {
            val defaultTeam = when (report.category) {
              FaultCategory.PROJECTOR -> MaintenanceTeam.IT_AV
              FaultCategory.LIGHTING -> MaintenanceTeam.ELECTRICAL
              FaultCategory.PLUMBING -> MaintenanceTeam.PLUMBING
              FaultCategory.FURNITURE -> MaintenanceTeam.CARPENTRY
              FaultCategory.AIR_CONDITIONING -> MaintenanceTeam.ELECTRICAL
              else -> MaintenanceTeam.ELECTRICAL
            }
            viewModel.assignTeam(report.id, defaultTeam, profile?.fullName)
          },
          onAssignWorker = {
            reportForWorkerAssignment = report
          },
          onMarkFixing = {
            viewModel.updateReportStatus(report.id, ReportStatus.FIXING)
          },
          onMarkResolved = {
            viewModel.updateReportStatus(report.id, ReportStatus.RESOLVED)
          }
        )
      }
    }

    // Lecture Theatre Hotspot Radar Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Lecture Theatre Hotspots",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SkillAlleyTextPrimary
              )
              Text(
                text = "Recurring fault radar across UCC venues",
                fontSize = 11.sp,
                color = SkillAlleyTextSecondary
              )
            }

            Surface(
              color = SkillAlleyBg,
              shape = RoundedCornerShape(100.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
            ) {
              Text(
                text = "Inspection Radar",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = SkillAlleyTextPrimary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          rooms.forEach { room ->
            val roomFaults = reports.count { it.roomCode == room.roomCode && it.status != ReportStatus.RESOLVED }
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .background(
                      if (roomFaults > 0) Color(0xFFEF4444) else Color(0xFF10B981),
                      CircleShape
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "${room.roomName} (${room.roomCode})",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = SkillAlleyTextPrimary
                )
              }

              Surface(
                color = if (roomFaults > 0) Color(0xFFFEE2E2) else SkillAlleyBg,
                shape = RoundedCornerShape(100.dp)
              ) {
                Text(
                  text = if (roomFaults > 0) "$roomFaults Active Faults" else "Clean",
                  color = if (roomFaults > 0) Color(0xFFDC2626) else Color(0xFF047857),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

/**
 * Worker Operations Metric Card
 */
@Composable
private fun WorkerMetricCard(
  title: String,
  value: String,
  subtitle: String,
  badgeText: String?,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 11.sp,
          color = SkillAlleyTextSecondary,
          fontWeight = FontWeight.Medium
        )
        if (badgeText != null) {
          Surface(
            color = SkillAlleyButtonDark,
            shape = CircleShape,
            modifier = Modifier.size(20.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = badgeText,
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = value,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = SkillAlleyTextPrimary
      )

      Text(
        text = subtitle,
        fontSize = 9.sp,
        color = SkillAlleyTextMuted,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

/**
 * Worker Job Card with 1-Tap Status Actions & Admin Assignment
 */
@Composable
private fun WorkerJobDispatchCard(
  report: FaultReport,
  onOpenDetail: () -> Unit,
  onAssignToMe: () -> Unit,
  onMarkFixing: () -> Unit,
  onMarkResolved: () -> Unit,
  isAdmin: Boolean = false,
  onAssignWorker: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onOpenDetail() },
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = report.category.displayName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = SkillAlleyBg,
            shape = RoundedCornerShape(100.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
          ) {
            Text(
              text = report.roomCode,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = SkillAlleyTextPrimary,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Surface(
          color = report.status.bgColor,
          shape = RoundedCornerShape(100.dp)
        ) {
          Text(
            text = report.status.displayName,
            color = report.status.textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "${report.roomName} • Reported by ${report.reporterName}",
        fontSize = 11.sp,
        color = SkillAlleyTextSecondary
      )

      if (!report.assignedTechnician.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .background(Color(0xFF10B981), CircleShape)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "Assigned to ${report.assignedTechnician} • ${report.assignedTeam?.displayName ?: "Works Directorate"}",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF047857)
          )
        }
      }

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = report.description,
        fontSize = 12.sp,
        color = SkillAlleyTextMuted,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Status Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        when (report.status) {
          ReportStatus.OPEN -> {
            if (isAdmin && onAssignWorker != null) {
              Button(
                onClick = onAssignWorker,
                colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Assign Worker ->", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            } else {
              Button(
                onClick = onAssignToMe,
                colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Assign to Me ->", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
          ReportStatus.ASSIGNED -> {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              if (isAdmin && onAssignWorker != null) {
                OutlinedButton(
                  onClick = onAssignWorker,
                  shape = RoundedCornerShape(100.dp),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("Reassign", fontSize = 10.sp, color = SkillAlleyTextPrimary)
                }
              }
              Button(
                onClick = onMarkFixing,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier.weight(1.3f)
              ) {
                Text("Start Fixing ->", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
          ReportStatus.FIXING -> {
            Button(
              onClick = onMarkResolved,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
              shape = RoundedCornerShape(100.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("Mark Resolved ✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
          ReportStatus.RESOLVED -> {
            // Already resolved
          }
        }

        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .clickable { onOpenDetail() },
          color = SkillAlleyBg,
          shape = RoundedCornerShape(100.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
        ) {
          Text(
            text = "Details",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = SkillAlleyTextPrimary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
          )
        }
      }
    }
  }
}

/**
 * Online Worker Roster Card for Admin View
 */
@Composable
fun OnlineWorkerRosterCard(
  worker: OnlineWorker,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyBg),
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
    modifier = modifier.width(160.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(SkillAlleyButtonDark),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = worker.avatarInitials,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Surface(
          color = if (worker.isOnline) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFE2E8F0),
          shape = RoundedCornerShape(100.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .background(if (worker.isOnline) Color(0xFF10B981) else Color(0xFF64748B), CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (worker.isOnline) "Ready" else "Off",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = if (worker.isOnline) Color(0xFF047857) else Color(0xFF64748B)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = worker.name,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = SkillAlleyTextPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Text(
        text = worker.team,
        fontSize = 10.sp,
        color = SkillAlleyTextSecondary,
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "${worker.activeJobsCount} Active Jobs",
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (worker.activeJobsCount > 1) Color(0xFFF59E0B) else Color(0xFF10B981)
      )
    }
  }
}

/**
 * Admin Dialog to Assign a Specific Online Worker to a Fault Report
 */
@Composable
fun AdminAssignWorkerDialog(
  report: FaultReport,
  onlineWorkers: List<OnlineWorker>,
  onAssign: (OnlineWorker) -> Unit,
  onDismiss: () -> Unit
) {
  androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("admin_assign_worker_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = SkillAlleyButtonDark,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "DISPATCH",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Assign UCC Worker",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = SkillAlleyTextPrimary
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = SkillAlleyTextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Fault Info Card
        Surface(
          color = SkillAlleyBg,
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "${report.category.displayName} in ${report.roomName} (${report.roomCode})",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "\"${report.description}\"",
              fontSize = 11.sp,
              color = SkillAlleyTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Urgency: ${report.urgency.displayName} • Reported by: ${report.reporterName}",
              fontSize = 10.sp,
              color = report.urgency.textColor,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Select Online Technician from Works Dept:",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = SkillAlleyTextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          onlineWorkers.forEach { worker ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = SkillAlleyBg),
              border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onAssign(worker) }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(CircleShape)
                      .background(SkillAlleyButtonDark),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = worker.avatarInitials,
                      color = Color.White,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = worker.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SkillAlleyTextPrimary
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Box(
                        modifier = Modifier
                          .size(7.dp)
                          .background(
                            if (worker.isOnline) Color(0xFF10B981) else Color(0xFF94A3B8),
                            CircleShape
                          )
                      )
                    }
                    Text(
                      text = "${worker.team} • ${worker.staffCode}",
                      fontSize = 11.sp,
                      color = SkillAlleyTextSecondary
                    )
                    Text(
                      text = "${worker.activeJobsCount} Active Jobs",
                      fontSize = 10.sp,
                      color = if (worker.activeJobsCount > 2) Color(0xFFEF4444) else Color(0xFF10B981),
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }

                Button(
                  onClick = { onAssign(worker) },
                  colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
                  shape = RoundedCornerShape(100.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text("Assign", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(100.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Cancel", color = SkillAlleyTextPrimary, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

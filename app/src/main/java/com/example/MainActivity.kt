package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FaultCategory
import com.example.data.model.UserRole
import com.example.ui.components.AuthDialog
import com.example.ui.components.QrScanDialog
import com.example.ui.components.ReportDetailDialog
import com.example.ui.components.RoomQrDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyReportsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReportFaultScreen
import com.example.ui.screens.StaffQueueScreen
import com.example.ui.screens.UccAuthScreen
import com.example.ui.screens.UccSignInScreen
import com.example.ui.theme.CampusFixTheme
import com.example.ui.theme.SkillAlleyBg
import com.example.ui.theme.SkillAlleyBorder
import com.example.ui.theme.SkillAlleyButtonDark
import com.example.ui.theme.SkillAlleyCard
import com.example.ui.theme.SkillAlleyTextMuted
import com.example.ui.theme.SkillAlleyTextPrimary
import com.example.ui.theme.SkillAlleyTextSecondary
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonGradientEnd
import com.example.ui.theme.NeonGradientStart
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextNeonHighlight
import com.example.ui.theme.TextNeonPrimary
import com.example.ui.theme.TextNeonSecondary
import com.example.ui.theme.TextNeonTertiary
import com.example.ui.viewmodel.CampusFixViewModel
import com.example.ui.viewmodel.NavigationTab
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
  private val viewModel: CampusFixViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      CampusFixTheme {
        CampusFixApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun CampusFixApp(viewModel: CampusFixViewModel) {
  val activeTab by viewModel.activeTab.collectAsState()
  val profile by viewModel.currentProfile.collectAsState()
  val rooms by viewModel.allRooms.collectAsState()
  val selectedRoomForReport by viewModel.selectedRoomForReport.collectAsState()
  val showQrScan by viewModel.showQrScanDialog.collectAsState()
  val showRoomQr by viewModel.showRoomQrModal.collectAsState()
  val selectedReportDetail by viewModel.selectedReportDetail.collectAsState()
  val showAuth by viewModel.showAuthDialog.collectAsState()
  val successMsg by viewModel.reportSuccessMessage.collectAsState()

  // STRICT REQUIREMENT: No one is signed in until they actually sign in
  if (profile == null) {
    UccAuthScreen(
      onAuthenticate = { studentId, name, email, role, department ->
        viewModel.signInOrUpdateProfile(studentId, name, email, role, department)
      }
    )
    return
  }

  // Track if we are currently inside the Report Fault flow
  var isReportingFault by remember { mutableStateOf(false) }
  var preselectedCategory by remember { mutableStateOf<FaultCategory?>(null) }
  var preselectedImage by remember { mutableStateOf<Int?>(null) }

  // When room is scanned or selected for report, open the report screen
  LaunchedEffect(selectedRoomForReport) {
    if (selectedRoomForReport != null) {
      isReportingFault = true
    }
  }

  // Handle system back navigation
  BackHandler(enabled = isReportingFault || activeTab != NavigationTab.HOME) {
    if (isReportingFault) {
      isReportingFault = false
      preselectedCategory = null
      preselectedImage = null
      viewModel.clearSelectedRoom()
    } else {
      viewModel.setTab(NavigationTab.HOME)
    }
  }

  // Auto-dismiss success toast
  LaunchedEffect(successMsg) {
    if (successMsg != null) {
      delay(4000)
      viewModel.clearSuccessMessage()
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      if (!isReportingFault) {
        SkillAlleyBottomNavBar(
          activeTab = activeTab,
          userRole = profile?.role ?: UserRole.STUDENT,
          onTabSelected = { tab ->
            viewModel.setTab(tab)
          },
          onCenterAddClick = {
            viewModel.openQrScanner()
          }
        )
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .statusBarsPadding()
    ) {
      // Screen Router
      if (isReportingFault) {
        ReportFaultScreen(
          viewModel = viewModel,
          initialCategory = preselectedCategory,
          initialSampleImage = preselectedImage,
          onBack = {
            isReportingFault = false
            preselectedCategory = null
            preselectedImage = null
            viewModel.clearSelectedRoom()
          }
        )
      } else {
        when (activeTab) {
          NavigationTab.HOME -> {
            HomeScreen(
              viewModel = viewModel,
              onNavigateToReport = { cat, img ->
                preselectedCategory = cat
                preselectedImage = img
                isReportingFault = true
              },
              onViewAllReports = {
                viewModel.setTab(NavigationTab.MY_REPORTS)
              }
            )
          }
          NavigationTab.MY_REPORTS -> {
            MyReportsScreen(
              viewModel = viewModel,
              onNewReportClick = {
                viewModel.openQrScanner()
              }
            )
          }
          NavigationTab.QUEUE -> {
            StaffQueueScreen(viewModel = viewModel)
          }
          NavigationTab.DASHBOARD -> {
            DashboardScreen(viewModel = viewModel)
          }
          NavigationTab.PROFILE -> {
            ProfileScreen(viewModel = viewModel)
          }
        }
      }

      // Success Notification Banner
      AnimatedVisibility(
        visible = successMsg != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Surface(
          color = Color(0xFF065F46),
          shape = RoundedCornerShape(14.dp),
          shadowElevation = 8.dp,
          border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF10B981)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF34D399),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = successMsg ?: "",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
            IconButton(
              onClick = { viewModel.clearSuccessMessage() },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
            }
          }
        }
      }

      // QR Scanner Dialog
      if (showQrScan) {
        QrScanDialog(
          availableRooms = rooms,
          onRoomScanned = { room ->
            viewModel.onRoomScanned(room)
            isReportingFault = true
          },
          onDismiss = {
            viewModel.closeQrScanner()
          }
        )
      }

      // Room QR Placard Dialog
      showRoomQr?.let { room ->
        RoomQrDialog(
          initialRoom = room,
          allRooms = rooms,
          onDismiss = { viewModel.showRoomQr(null) },
          onScanThisRoom = { scanned ->
            viewModel.onRoomScanned(scanned)
            isReportingFault = true
          }
        )
      }

      // Report Detail Dialog
      selectedReportDetail?.let { report ->
        ReportDetailDialog(
          report = report,
          userRole = profile?.role,
          onDismiss = { viewModel.selectReportDetail(null) },
          onAssignTeam = { team ->
            viewModel.assignTeam(report.id, team, null)
          },
          onUpdateStatus = { nextStatus ->
            viewModel.updateReportStatus(report.id, nextStatus)
          },
          onDeleteReport = {
            viewModel.deleteReport(report.id)
          }
        )
      }

      // UCC Student Sign In Dialog
      if (showAuth) {
        AuthDialog(
          currentStudentId = profile?.studentOrStaffId,
          currentEmail = profile?.email,
          currentName = profile?.fullName,
          currentRole = profile?.role ?: UserRole.STUDENT,
          onDismiss = { viewModel.showAuthModal(false) },
          onSignIn = { studentId, name, email, role, department ->
            viewModel.signInOrUpdateProfile(studentId, name, email, role, department)
          }
        )
      }
    }
  }
}

/**
 * SkillAlley Clean White Bottom Navigation Bar
 * Matching reference screenshot (pure white container, subtle border, charcoal pill highlights)
 */
@Composable
fun SkillAlleyBottomNavBar(
  activeTab: NavigationTab,
  userRole: UserRole,
  onTabSelected: (NavigationTab) -> Unit,
  onCenterAddClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isWorker = userRole == UserRole.WORKER

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .shadow(elevation = 12.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
      .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
    color = SkillAlleyCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Tab 1: Home / Ops Desk
        SkillAlleyNavItem(
          title = if (isWorker) "Ops Desk" else "Home",
          activeIcon = Icons.Filled.Home,
          inactiveIcon = Icons.Outlined.Home,
          isSelected = activeTab == NavigationTab.HOME,
          onClick = { onTabSelected(NavigationTab.HOME) },
          testTag = "nav_home"
        )

        // Tab 2: My Reports / All Jobs
        SkillAlleyNavItem(
          title = if (isWorker) "All Jobs" else "Reports",
          activeIcon = Icons.Filled.Assignment,
          inactiveIcon = Icons.Outlined.Assignment,
          isSelected = if (isWorker) activeTab == NavigationTab.QUEUE else activeTab == NavigationTab.MY_REPORTS,
          onClick = {
            if (isWorker) {
              onTabSelected(NavigationTab.QUEUE)
            } else {
              onTabSelected(NavigationTab.MY_REPORTS)
            }
          },
          testTag = "nav_reports"
        )

        // Center Floating Charcoal '+' Button (Report Fault / Quick Scan)
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(SkillAlleyButtonDark)
            .clickable { onCenterAddClick() }
            .testTag("nav_center_add"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Report Fault",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
          )
        }

        // Tab 4: Analytics
        SkillAlleyNavItem(
          title = "Analytics",
          activeIcon = Icons.Filled.Assessment,
          inactiveIcon = Icons.Outlined.Assessment,
          isSelected = activeTab == NavigationTab.DASHBOARD,
          onClick = { onTabSelected(NavigationTab.DASHBOARD) },
          testTag = "nav_dashboard"
        )

        // Tab 5: Profile (Student ID / Staff Pass)
        SkillAlleyNavItem(
          title = if (isWorker) "Staff ID" else "Student ID",
          activeIcon = Icons.Filled.Person,
          inactiveIcon = Icons.Outlined.Person,
          isSelected = activeTab == NavigationTab.PROFILE,
          onClick = { onTabSelected(NavigationTab.PROFILE) },
          testTag = "nav_profile"
        )
      }
    }
  }
}

@Composable
private fun SkillAlleyNavItem(
  title: String,
  activeIcon: ImageVector,
  inactiveIcon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  val interactionSource = remember { MutableInteractionSource() }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable(
        interactionSource = interactionSource,
        indication = null
      ) { onClick() }
      .padding(vertical = 4.dp, horizontal = 8.dp)
      .testTag(testTag)
  ) {
    if (isSelected) {
      Surface(
        color = SkillAlleyButtonDark,
        shape = RoundedCornerShape(100.dp),
        modifier = Modifier.padding(bottom = 2.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = activeIcon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    } else {
      Icon(
        imageVector = inactiveIcon,
        contentDescription = title,
        tint = SkillAlleyTextSecondary,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.height(3.dp))
      Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = SkillAlleyTextSecondary
      )
    }
  }
}

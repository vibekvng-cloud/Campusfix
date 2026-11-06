package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampusRoom
import com.example.data.model.MaintenanceTeam
import com.example.data.model.ReportStatus
import com.example.ui.components.FaultOverviewDonutCard
import com.example.ui.theme.SkillAlleyBg
import com.example.ui.theme.SkillAlleyBorder
import com.example.ui.theme.SkillAlleyButtonDark
import com.example.ui.theme.SkillAlleyCard
import com.example.ui.theme.SkillAlleyTextMuted
import com.example.ui.theme.SkillAlleyTextPrimary
import com.example.ui.theme.SkillAlleyTextSecondary
import com.example.ui.theme.UccGold
import com.example.ui.viewmodel.CampusFixViewModel

@Composable
fun DashboardScreen(
  viewModel: CampusFixViewModel,
  modifier: Modifier = Modifier
) {
  val reports by viewModel.allReports.collectAsState()
  val rooms by viewModel.allRooms.collectAsState()

  val openCount = reports.count { it.status == ReportStatus.OPEN }
  val assignedCount = reports.count { it.status == ReportStatus.ASSIGNED }
  val fixingCount = reports.count { it.status == ReportStatus.FIXING }
  val resolvedCount = reports.count { it.status == ReportStatus.RESOLVED }
  val total = reports.size

  val resolvedPercent = if (total > 0) (resolvedCount * 100 / total) else 0

  val categoryMap = com.example.data.model.FaultCategory.entries.associateWith { cat ->
    reports.count { it.category == cat }
  }

  val reportsPerRoom = reports.groupBy { it.roomCode }
  val sortedRoomsByFaults = rooms.sortedByDescending { room ->
    val countFromReports = reportsPerRoom[room.roomCode]?.size ?: 0
    maxOf(room.recurringFaultCount, countFromReports)
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(SkillAlleyBg)
      .testTag("dashboard_screen"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "UCC Maintenance Analytics",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = SkillAlleyButtonDark,
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "SLA DESK",
              color = Color.White,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
        Text(
          text = "Recurring fault tracking for Sandwich, NLT, CALC, LT & CODE",
          fontSize = 12.sp,
          color = SkillAlleyTextSecondary
        )
      }
    }

    // Key Performance Metrics Grid (SkillAlley Style)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        KpiCard(
          title = "UCC Avg Fix Time",
          value = "38 min",
          subtitle = "-12m vs last semester",
          badge = "85",
          modifier = Modifier.weight(1f)
        )
        KpiCard(
          title = "Resolution Rate",
          value = "$resolvedPercent%",
          subtitle = "$resolvedCount tickets fixed",
          badge = "75",
          modifier = Modifier.weight(1f)
        )
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        KpiCard(
          title = "Open Backlog",
          value = "$openCount jobs",
          subtitle = "Awaiting technician",
          badge = null,
          modifier = Modifier.weight(1f)
        )
        KpiCard(
          title = "Active Repairs",
          value = "${assignedCount + fixingCount} jobs",
          subtitle = "Teams in lecture halls",
          badge = "LIVE",
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Donut breakdown
    item {
      FaultOverviewDonutCard(
        categoryCounts = categoryMap,
        totalCount = total
      )
    }

    // Recurring Faults by Theatre (UCC Specific)
    item {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Recurring Faults in UCC Theatres",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )
          Surface(
            color = Color(0xFFFEE2E2),
            shape = RoundedCornerShape(100.dp)
          ) {
            Text(
              text = "Inspection Priority",
              color = Color(0xFFDC2626),
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Theatres with repeated complaints. Used by UCC Works for preventive repairs.",
          fontSize = 11.sp,
          color = SkillAlleyTextSecondary
        )
      }
    }

    items(sortedRoomsByFaults) { room ->
      val count = reportsPerRoom[room.roomCode]?.size ?: room.recurringFaultCount
      RoomRecurrenceCard(
        room = room,
        faultCount = count,
        onViewQr = { viewModel.showRoomQr(room) }
      )
    }

    // UCC Maintenance Team Workloads
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "UCC Maintenance Team Workload",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )
          Text(
            text = "Active assignments per specialist trade unit",
            fontSize = 11.sp,
            color = SkillAlleyTextSecondary
          )

          Spacer(modifier = Modifier.height(14.dp))

          MaintenanceTeam.entries.forEach { team ->
            val teamReports = reports.filter { it.assignedTeam == team }
            val teamResolved = teamReports.count { it.status == ReportStatus.RESOLVED }
            val teamTotal = teamReports.size
            val teamProgress = if (teamTotal > 0) teamResolved.toFloat() / teamTotal.toFloat() else 0f

            Column(modifier = Modifier.padding(vertical = 5.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = team.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SkillAlleyTextPrimary
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "(${team.leadName})",
                    fontSize = 10.sp,
                    color = SkillAlleyTextMuted
                  )
                }
                Text(
                  text = "$teamResolved/$teamTotal Fixed",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = SkillAlleyButtonDark
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              LinearProgressIndicator(
                progress = { teamProgress },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(100.dp)),
                color = SkillAlleyButtonDark,
                trackColor = SkillAlleyBg,
                strokeCap = StrokeCap.Round
              )
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

@Composable
private fun KpiCard(
  title: String,
  value: String,
  subtitle: String,
  badge: String?,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
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
        if (badge != null) {
          Surface(
            color = SkillAlleyButtonDark,
            shape = CircleShape,
            modifier = Modifier.size(20.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = badge,
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = SkillAlleyTextPrimary
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        fontSize = 10.sp,
        color = SkillAlleyTextMuted,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
private fun RoomRecurrenceCard(
  room: CampusRoom,
  faultCount: Int,
  onViewQr: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (faultCount >= 4) Color(0xFFFEE2E2) else SkillAlleyBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (faultCount >= 4) Icons.Filled.Warning else Icons.Filled.LocationOn,
            contentDescription = null,
            tint = if (faultCount >= 4) Color(0xFFDC2626) else SkillAlleyTextPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "${room.roomName} (${room.roomCode})",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = SkillAlleyTextPrimary
          )
          Text(
            text = "${room.building} • ${room.floor}",
            fontSize = 11.sp,
            color = SkillAlleyTextSecondary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$faultCount repeated fault reports",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (faultCount >= 4) Color(0xFFDC2626) else SkillAlleyTextSecondary
          )
        }
      }

      Surface(
        color = SkillAlleyButtonDark,
        shape = RoundedCornerShape(100.dp),
        modifier = Modifier.clickable { onViewQr() }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Filled.QrCode,
            contentDescription = "QR Code",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "QR Sticker",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}

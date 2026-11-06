package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FaultReport
import com.example.data.model.MaintenanceTeam
import com.example.data.model.ReportStatus
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
fun StaffQueueScreen(
  viewModel: CampusFixViewModel,
  modifier: Modifier = Modifier
) {
  val reports by viewModel.allReports.collectAsState()
  var selectedTeamFilter by remember { mutableStateOf<MaintenanceTeam?>(null) }
  var selectedStatusFilter by remember { mutableStateOf<ReportStatus?>(null) }

  val unassignedCount = reports.count { it.status == ReportStatus.OPEN && it.assignedTeam == null }

  val filteredReports = reports.filter { report ->
    val matchesTeam = selectedTeamFilter == null || report.assignedTeam == selectedTeamFilter
    val matchesStatus = selectedStatusFilter == null || report.status == selectedStatusFilter
    matchesTeam && matchesStatus
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(SkillAlleyBg)
      .testTag("staff_queue_screen"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    item {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "UCC Maintenance Dispatch",
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
              text = "WORKS DEPT",
              color = Color.White,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
        Text(
          text = "Dispatch teams to Sandwich, NLT, CALC, LT & CODE",
          fontSize = 12.sp,
          color = SkillAlleyTextSecondary
        )
      }
    }

    // Unassigned Alert Banner
    if (unassignedCount > 0) {
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFCA5A5))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF4444)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "$unassignedCount Open Jobs Require UCC Dispatch",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF991B1B)
              )
              Text(
                text = "Assign to IT & AV, Electrical, Plumbing or Carpentry",
                fontSize = 11.sp,
                color = Color(0xFFDC2626)
              )
            }
          }
        }
      }
    }

    // Team Filter Chips
    item {
      Column {
        Text(
          text = "Filter by UCC Team:",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = SkillAlleyTextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          item {
            val isAll = selectedTeamFilter == null
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .clickable { selectedTeamFilter = null },
              shape = RoundedCornerShape(100.dp),
              color = if (isAll) SkillAlleyButtonDark else SkillAlleyCard,
              border = if (isAll) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
            ) {
              Text(
                text = "All UCC Teams",
                fontSize = 11.sp,
                fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium,
                color = if (isAll) Color.White else SkillAlleyTextPrimary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
              )
            }
          }

          items(MaintenanceTeam.entries) { team ->
            val isSelected = selectedTeamFilter == team
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .clickable { selectedTeamFilter = team },
              shape = RoundedCornerShape(100.dp),
              color = if (isSelected) SkillAlleyButtonDark else SkillAlleyCard,
              border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
            ) {
              Text(
                text = team.code,
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

    // Status Filter Chips
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        item {
          val isAll = selectedStatusFilter == null
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(100.dp))
              .clickable { selectedStatusFilter = null },
            shape = RoundedCornerShape(100.dp),
            color = if (isAll) SkillAlleyButtonDark else SkillAlleyCard,
            border = if (isAll) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
          ) {
            Text(
              text = "All Statuses",
              fontSize = 11.sp,
              fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium,
              color = if (isAll) Color.White else SkillAlleyTextPrimary,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
            )
          }
        }

        items(ReportStatus.entries) { status ->
          val isSelected = selectedStatusFilter == status
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(100.dp))
              .clickable { selectedStatusFilter = status },
            shape = RoundedCornerShape(100.dp),
            color = if (isSelected) SkillAlleyButtonDark else SkillAlleyCard,
            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
          ) {
            Text(
              text = status.displayName,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else SkillAlleyTextPrimary,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
            )
          }
        }
      }
    }

    // Results header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${filteredReports.size} Jobs in Dispatch Queue",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = SkillAlleyTextPrimary
        )
      }
    }

    // Job Items
    if (filteredReports.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
        ) {
          Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Filled.CheckCircle,
              contentDescription = null,
              tint = Color(0xFF10B981),
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No jobs in this category",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = SkillAlleyTextPrimary
            )
            Text(
              text = "Everything in this filter is cleared",
              fontSize = 11.sp,
              color = SkillAlleyTextSecondary
            )
          }
        }
      }
    } else {
      items(filteredReports, key = { it.id }) { report ->
        StaffJobCard(
          report = report,
          onCardClick = { viewModel.selectReportDetail(report) },
          onAssignTeam = { team ->
            viewModel.assignTeam(report.id, team, null)
          },
          onAdvanceStatus = { nextStatus ->
            viewModel.updateReportStatus(report.id, nextStatus)
          }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

@Composable
private fun StaffJobCard(
  report: FaultReport,
  onCardClick: () -> Unit,
  onAssignTeam: (MaintenanceTeam) -> Unit,
  onAdvanceStatus: (ReportStatus) -> Unit
) {
  var showTeamPicker by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCardClick() }
      .testTag("staff_job_card_${report.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top row: Room & Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (report.sampleImageRes != null) {
            Image(
              painter = painterResource(id = report.sampleImageRes),
              contentDescription = null,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, SkillAlleyBorder, RoundedCornerShape(10.dp))
            )
          } else {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(SkillAlleyBg)
                .border(1.dp, SkillAlleyBorder, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = report.category.icon,
                contentDescription = null,
                tint = SkillAlleyTextPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "${report.roomName} (${report.roomCode})",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = SkillAlleyTextPrimary
            )
            Text(
              text = report.building,
              fontSize = 11.sp,
              color = SkillAlleyTextSecondary
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
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = report.description,
        fontSize = 12.sp,
        color = SkillAlleyTextSecondary,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = SkillAlleyTextMuted,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = report.reporterName,
            fontSize = 11.sp,
            color = SkillAlleyTextMuted
          )
        }

        Surface(
          color = report.urgency.bgColor,
          shape = RoundedCornerShape(100.dp)
        ) {
          Text(
            text = report.urgency.displayName,
            color = report.urgency.textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Assigned team or action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (report.assignedTeam != null) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.Engineering,
              contentDescription = null,
              tint = SkillAlleyTextPrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = report.assignedTeam.code,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SkillAlleyTextPrimary
              )
              Text(
                text = report.assignedTechnician ?: report.assignedTeam.leadName,
                fontSize = 10.sp,
                color = SkillAlleyTextMuted
              )
            }
          }
        } else {
          Text(
            text = "Unassigned",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEF4444)
          )
        }

        when (report.status) {
          ReportStatus.OPEN -> {
            Button(
              onClick = { showTeamPicker = !showTeamPicker },
              colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
              shape = RoundedCornerShape(100.dp),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
              modifier = Modifier.testTag("assign_team_btn_${report.id}")
            ) {
              Text("Assign Team ->", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }
          ReportStatus.ASSIGNED -> {
            Button(
              onClick = { onAdvanceStatus(ReportStatus.FIXING) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
              shape = RoundedCornerShape(100.dp),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
              modifier = Modifier.testTag("start_fixing_btn_${report.id}")
            ) {
              Text("Start Fixing ->", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }
          ReportStatus.FIXING -> {
            Button(
              onClick = { onAdvanceStatus(ReportStatus.RESOLVED) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
              shape = RoundedCornerShape(100.dp),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
              modifier = Modifier.testTag("mark_resolved_btn_${report.id}")
            ) {
              Text("Mark Resolved ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }
          ReportStatus.RESOLVED -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Fixed & Tested",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
              )
            }
          }
        }
      }

      // Quick team selector dropdown row
      if (showTeamPicker) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          color = SkillAlleyBg,
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "Dispatch to UCC Works Section:",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              MaintenanceTeam.entries.forEach { team ->
                Surface(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                      onAssignTeam(team)
                      showTeamPicker = false
                    },
                  shape = RoundedCornerShape(8.dp),
                  color = SkillAlleyCard,
                  border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
                ) {
                  Text(
                    text = team.code,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkillAlleyTextPrimary,
                    modifier = Modifier.padding(vertical = 6.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

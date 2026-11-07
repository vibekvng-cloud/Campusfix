package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FaultReport
import com.example.data.model.MaintenanceTeam
import com.example.data.model.OnlineWorker
import com.example.data.model.ReportStatus
import com.example.data.model.UserRole
import com.example.ui.theme.SkillAlleyBg
import com.example.ui.theme.SkillAlleyBorder
import com.example.ui.theme.SkillAlleyButtonDark
import com.example.ui.theme.SkillAlleyCard
import com.example.ui.theme.SkillAlleyTextMuted
import com.example.ui.theme.SkillAlleyTextPrimary
import com.example.ui.theme.SkillAlleyTextSecondary
import com.example.ui.theme.UccGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportDetailDialog(
  report: FaultReport,
  userRole: UserRole?,
  onlineWorkers: List<OnlineWorker> = emptyList(),
  onDismiss: () -> Unit,
  onAssignTeam: (MaintenanceTeam) -> Unit,
  onAssignWorker: ((OnlineWorker) -> Unit)? = null,
  onUpdateStatus: (ReportStatus) -> Unit,
  onDeleteReport: () -> Unit
) {
  val timeFormat = SimpleDateFormat("h:mm a • MMMM d, yyyy", Locale.getDefault())
  val createdDate = timeFormat.format(Date(report.createdAtMillis))
  val resolvedDate = report.resolvedAtMillis?.let { timeFormat.format(Date(it)) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("report_detail_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Top row: Status & Close
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = report.status.bgColor,
            shape = RoundedCornerShape(100.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, report.status.textColor.copy(alpha = 0.4f))
          ) {
            Text(
              text = "STAGE: ${report.status.displayName.uppercase()}",
              color = report.status.textColor,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(30.dp)
          ) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = "Close",
              tint = SkillAlleyTextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        var show3DModel by remember { mutableStateOf(false) }

        // Mode Switcher: Photo Evidence vs 3D Simulation
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (show3DModel) "Interactive 3D Simulation" else "Attached Photo Evidence",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextSecondary
          )

          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(100.dp))
              .clickable { show3DModel = !show3DModel },
            color = if (show3DModel) SkillAlleyButtonDark else SkillAlleyBg,
            border = if (show3DModel) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
          ) {
            Text(
              text = if (show3DModel) "View Photo" else "View 3D Animation",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (show3DModel) Color.White else SkillAlleyTextPrimary,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        if (show3DModel) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp)
              .clip(RoundedCornerShape(18.dp))
              .background(SkillAlleyBg)
              .border(1.dp, SkillAlleyBorder, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
          ) {
            Animated3DReportVisual(
              category = report.category,
              size = 140.dp,
              interactive = true
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
        } else if (report.sampleImageRes != null) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp)
              .clip(RoundedCornerShape(18.dp))
              .border(1.dp, SkillAlleyBorder, RoundedCornerShape(18.dp))
          ) {
            Image(
              painter = painterResource(id = report.sampleImageRes),
              contentDescription = report.description,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
        }

        // Title and room
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(SkillAlleyBg)
              .border(1.dp, SkillAlleyBorder, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = report.category.icon,
              contentDescription = null,
              tint = SkillAlleyTextPrimary,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = report.category.displayName,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = SkillAlleyTextPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = SkillAlleyTextPrimary,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "${report.roomName} (${report.roomCode})",
                fontSize = 12.sp,
                color = SkillAlleyTextSecondary,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4-Stage Progress Stepper
        Text(
          text = "UCC Repair Stage Tracker",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = SkillAlleyTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        StatusProgressStepper(
          currentStatus = report.status,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
          text = "Problem Description",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = SkillAlleyTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
          color = SkillAlleyBg,
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = report.description,
            fontSize = 13.sp,
            color = SkillAlleyTextPrimary,
            modifier = Modifier.padding(12.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Assigned team info
        if (report.assignedTeam != null) {
          Surface(
            color = SkillAlleyBg,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Filled.Engineering,
                contentDescription = null,
                tint = SkillAlleyTextPrimary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Assigned Team: ${report.assignedTeam.displayName}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = SkillAlleyTextPrimary
                )
                Text(
                  text = "Team Lead: ${report.assignedTeam.leadName} • UCC Works Directorate",
                  fontSize = 11.sp,
                  color = SkillAlleyTextSecondary
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
        }

        // Resolution Notes
        if (!report.resolutionNotes.isNullOrBlank()) {
          Surface(
            color = Color(0xFFF0FDF4),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "Works Directorate Fix Notes",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF166534)
              )
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = report.resolutionNotes,
                fontSize = 12.sp,
                color = Color(0xFF15803D)
              )
              if (resolvedDate != null) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = "Fixed: $resolvedDate",
                  fontSize = 10.sp,
                  color = Color(0xFF16A34A)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Timestamps & Reporter
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(text = "Reported By", fontSize = 10.sp, color = SkillAlleyTextMuted)
            Text(text = "${report.reporterName} (${report.reporterEmail})", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = SkillAlleyTextPrimary)
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(text = "Logged", fontSize = 10.sp, color = SkillAlleyTextMuted)
            Text(text = createdDate, fontSize = 10.sp, color = SkillAlleyTextSecondary)
          }
        }

        // Maintenance Action Controls
        if (userRole == UserRole.ADMIN) {
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "Directorate Admin Dispatch & Assignment",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))

          if (!report.assignedTechnician.isNullOrBlank()) {
            Surface(
              color = Color(0xFF10B981).copy(alpha = 0.1f),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Assigned Worker: ${report.assignedTechnician} (${report.assignedTeam?.displayName ?: "Works Directorate"})",
                  color = Color(0xFF047857),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }

          if (onlineWorkers.isNotEmpty() && onAssignWorker != null && report.status != ReportStatus.RESOLVED) {
            Text(
              text = "Assign to Available Works Personnel:",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              onlineWorkers.take(3).forEach { worker ->
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = SkillAlleyBg,
                  border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAssignWorker(worker) }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(7.dp)
                          .background(if (worker.isOnline) Color(0xFF10B981) else Color(0xFF94A3B8), CircleShape)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "${worker.name} • ${worker.team}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SkillAlleyTextPrimary
                      )
                    }
                    Text(
                      text = "Assign ->",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = SkillAlleyButtonDark
                    )
                  }
                }
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }

          // Admin status overrides
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (report.status != ReportStatus.FIXING && report.status != ReportStatus.RESOLVED) {
              Button(
                onClick = { onUpdateStatus(ReportStatus.FIXING) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Start Fixing ->", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
            if (report.status != ReportStatus.RESOLVED) {
              Button(
                onClick = { onUpdateStatus(ReportStatus.RESOLVED) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Mark Resolved ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        } else if (userRole == UserRole.WORKER) {
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "UCC Maintenance Dispatch Action",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (report.status == ReportStatus.OPEN) {
              Button(
                onClick = { onAssignTeam(MaintenanceTeam.IT_AV) },
                colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Assign IT/AV ->", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
              Button(
                onClick = { onAssignTeam(MaintenanceTeam.ELECTRICAL) },
                colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Assign Elec ->", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            } else if (report.status == ReportStatus.ASSIGNED) {
              Button(
                onClick = { onUpdateStatus(ReportStatus.FIXING) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("Start Fixing in ${report.roomCode} ->", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            } else if (report.status == ReportStatus.FIXING) {
              Button(
                onClick = { onUpdateStatus(ReportStatus.RESOLVED) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("Mark as Resolved & Tested ✓", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(100.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Close", color = SkillAlleyTextPrimary, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

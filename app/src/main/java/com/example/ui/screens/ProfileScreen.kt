package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.CampusQrCodeCanvas
import com.example.ui.theme.SkillAlleyBg
import com.example.ui.theme.SkillAlleyBorder
import com.example.ui.theme.SkillAlleyButtonDark
import com.example.ui.theme.SkillAlleyCard
import com.example.ui.theme.SkillAlleyDarkCard
import com.example.ui.theme.SkillAlleyDarkSurface
import com.example.ui.theme.SkillAlleyTextMuted
import com.example.ui.theme.SkillAlleyTextPrimary
import com.example.ui.theme.SkillAlleyTextSecondary
import com.example.ui.theme.UccGold
import com.example.ui.viewmodel.CampusFixViewModel

@Composable
fun ProfileScreen(
  viewModel: CampusFixViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.currentProfile.collectAsState()
  val reports by viewModel.allReports.collectAsState()

  val isWorker = profile?.role == UserRole.WORKER
  val userReports = reports.filter { it.reporterId == profile?.id }
  val resolvedCount = userReports.count { it.status == com.example.data.model.ReportStatus.RESOLVED }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(SkillAlleyBg)
      .testTag("profile_screen"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = if (isWorker) "UCC Staff Credential" else "UCC Student ID Card",
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
                text = "GHANA",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "Official University of Cape Coast Identity",
            fontSize = 11.sp,
            color = SkillAlleyTextSecondary
          )
        }

        OutlinedButton(
          onClick = { viewModel.signOut() },
          shape = RoundedCornerShape(100.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFEF4444)),
          modifier = Modifier.testTag("sign_out_button_header")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Sign Out", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Digital Credential Card (University of Cape Coast Ghana)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Box(
          modifier = Modifier
            .background(
              brush = if (isWorker) {
                Brush.linearGradient(
                  colors = listOf(
                    Color(0xFF1E293B),
                    Color(0xFF0F172A),
                    Color(0xFF1E1B4B)
                  )
                )
              } else {
                Brush.linearGradient(
                  colors = listOf(
                    Color(0xFF18181B),
                    Color(0xFF27272A),
                    Color(0xFF18181B)
                  )
                )
              }
            )
            .padding(20.dp)
        ) {
          Column {
            // UCC Emblem & Header
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(UccGold.copy(alpha = 0.2f))
                    .border(1.dp, UccGold, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isWorker) Icons.Filled.Engineering else Icons.Filled.School,
                    contentDescription = null,
                    tint = UccGold,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "UNIVERSITY OF CAPE COAST",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                  )
                  Text(
                    text = "VERITAS NOBIS LUMEN • GHANA",
                    color = UccGold,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                  )
                }
              }

              Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(100.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
              ) {
                Text(
                  text = if (isWorker) "WORKS DEPT" else (profile?.role?.badge ?: "Undergraduate"),
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Information
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = profile?.fullName ?: "Kwame Mensah",
                  color = Color.White,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = if (isWorker) "STAFF CODE: " else "STUDENT ID: ",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                  )
                  Text(
                    text = profile?.studentOrStaffId ?: if (isWorker) "STAFF/0891" else "PS/CSC/22/0199",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = profile?.email ?: if (isWorker) "works.ucc@gmail.com" else "student@stu.ucc.edu.gh",
                  color = Color(0xFFCBD5E1),
                  fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = profile?.department ?: if (isWorker) "Works Directorate" else "Dept. of Computer Science & IT",
                  color = Color.White.copy(alpha = 0.9f),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
                if (!isWorker) {
                  Text(
                    text = "Hall: ${profile?.hallOfResidence ?: "Casely Hayford Hall"}",
                    color = UccGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }

              // Authentic QR Code Badge
              CampusQrCodeCanvas(
                payload = if (isWorker) {
                  "UCC_STAFF:${profile?.studentOrStaffId ?: "STAFF/0891"}"
                } else {
                  "UCC_STUDENT:${profile?.studentOrStaffId ?: "PS/CSC/22/0199"}"
                },
                size = 72.dp,
                dotColor = Color(0xFF09070F),
                backgroundColor = Color.White
              )
            }
          }
        }
      }
    }

    // Role & Security Card (In-app role switching removed per security policy: must sign out first)
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
        modifier = Modifier.fillMaxWidth().testTag("profile_role_security_card")
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
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(if (isWorker) Color(0xFFE0E7FF) else Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isWorker) Icons.Filled.Engineering else Icons.Filled.School,
                  contentDescription = null,
                  tint = if (isWorker) Color(0xFF4338CA) else Color(0xFFB45309),
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = if (isWorker) "UCC Works Department Account" else "UCC Student Account",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = SkillAlleyTextPrimary
                )
                Text(
                  text = "Active session authenticated",
                  fontSize = 11.sp,
                  color = SkillAlleyTextSecondary
                )
              }
            }

            Surface(
              color = SkillAlleyButtonDark,
              shape = RoundedCornerShape(100.dp)
            ) {
              Text(
                text = if (isWorker) "Worker" else "Student",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Surface(
            color = SkillAlleyBg,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = SkillAlleyTextSecondary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "To switch between Student and Works Dept accounts, you must sign out first using the button below.",
                fontSize = 11.sp,
                color = SkillAlleyTextSecondary,
                lineHeight = 15.sp
              )
            }
          }
        }
      }
    }

    // Actions: Update ID & Sign Out
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
          onClick = { viewModel.showAuthModal(true) },
          shape = RoundedCornerShape(100.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("enter_student_id_button")
        ) {
          Icon(
            imageVector = Icons.Filled.Badge,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Update ID Credentials",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color.White
          )
        }

        OutlinedButton(
          onClick = { viewModel.signOut() },
          shape = RoundedCornerShape(100.dp),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFEF4444)),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFEF4444)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("profile_sign_out_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Sign Out of UCC Account",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFFEF4444)
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

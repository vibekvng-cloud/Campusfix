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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderGlow
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGradientEnd
import com.example.ui.theme.NeonGradientStart
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextNeonHighlight
import com.example.ui.theme.TextNeonPrimary
import com.example.ui.theme.TextNeonSecondary
import com.example.ui.theme.TextNeonTertiary
import com.example.ui.theme.UccGold

@Composable
fun UccSignInScreen(
  onSignIn: (studentId: String, name: String, email: String, role: UserRole, department: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var studentId by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var fullName by remember { mutableStateOf("") }
  var department by remember { mutableStateOf("Dept. of Computer Science & IT (UCC)") }
  var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .testTag("ucc_sign_in_screen")
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. UCC Header & Crest
      item {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(UccGold.copy(alpha = 0.35f), Color(0xFF140F2A))
                )
              )
              .border(2.dp, UccGold, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Filled.School,
              contentDescription = "UCC Logo",
              tint = UccGold,
              modifier = Modifier.size(36.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "UNIVERSITY OF CAPE COAST",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = UccGold,
              letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = UccGold.copy(alpha = 0.2f),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "GHANA",
                color = UccGold,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "CampusFix UCC",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )

          Text(
            text = "Fault Reporting & Maintenance Portal for Lecture Theatres",
            fontSize = 12.sp,
            color = TextNeonSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
          )

          // Hall tags pill
          Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("Sandwich", "NLT", "CALC", "LT", "CODE").forEach { hall ->
              Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, DarkBorder)
              ) {
                Text(
                  text = hall,
                  color = TextNeonTertiary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      // 2. Sign In Form Card
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, DarkBorderGlow),
          elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(bottom = 12.dp)
            ) {
              Icon(
                imageVector = Icons.Filled.VerifiedUser,
                contentDescription = null,
                tint = NeonMagenta,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Sign In With UCC Credentials",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextNeonPrimary
              )
            }

            // Student ID / Index Number
            Text(
              text = "Student ID / Staff Index Number *",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextNeonSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = studentId,
              onValueChange = {
                studentId = it
                errorMessage = null
              },
              placeholder = { Text("e.g. PS/CSC/21/0042", color = TextNeonTertiary, fontSize = 13.sp) },
              leadingIcon = {
                Icon(Icons.Filled.Badge, contentDescription = null, tint = NeonMagenta)
              },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signin_student_id_input"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonMagenta,
                unfocusedBorderColor = DarkBorder,
                focusedContainerColor = DarkSurfaceVariant,
                unfocusedContainerColor = DarkSurfaceVariant,
                focusedTextColor = TextNeonPrimary,
                unfocusedTextColor = TextNeonPrimary
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // School Email
            Text(
              text = "UCC Institutional Email *",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextNeonSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = email,
              onValueChange = {
                email = it
                errorMessage = null
              },
              placeholder = { Text("e.g. yourname@stu.ucc.edu.gh", color = TextNeonTertiary, fontSize = 13.sp) },
              leadingIcon = {
                Icon(Icons.Filled.Mail, contentDescription = null, tint = NeonMagenta)
              },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signin_email_input"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonMagenta,
                unfocusedBorderColor = DarkBorder,
                focusedContainerColor = DarkSurfaceVariant,
                unfocusedContainerColor = DarkSurfaceVariant,
                focusedTextColor = TextNeonPrimary,
                unfocusedTextColor = TextNeonPrimary
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Full Name
            Text(
              text = "Full Name",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextNeonSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = fullName,
              onValueChange = {
                fullName = it
                errorMessage = null
              },
              placeholder = { Text("e.g. Kwame Mensah", color = TextNeonTertiary, fontSize = 13.sp) },
              leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = null, tint = NeonMagenta)
              },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signin_name_input"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonMagenta,
                unfocusedBorderColor = DarkBorder,
                focusedContainerColor = DarkSurfaceVariant,
                unfocusedContainerColor = DarkSurfaceVariant,
                focusedTextColor = TextNeonPrimary,
                unfocusedTextColor = TextNeonPrimary
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // UCC Role Select
            Text(
              text = "Select Account Role",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextNeonSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              UserRole.entries.forEach { role ->
                val isSelected = role == selectedRole
                Surface(
                  modifier = Modifier
                    .weight(1f)
                    .clickable { selectedRole = role }
                    .testTag("signin_role_${role.name.lowercase()}"),
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) NeonMagenta else DarkSurfaceVariant,
                  border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                  Text(
                    text = when (role) {
                      UserRole.STUDENT -> "Student"
                      UserRole.WORKER -> "Works Dept"
                    },
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextNeonSecondary,
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                  )
                }
              }
            }

            if (errorMessage != null) {
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFF881337).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                  .border(1.dp, Color(0xFFF43F5E), RoundedCornerShape(8.dp))
                  .padding(horizontal = 10.dp, vertical = 8.dp)
              ) {
                Icon(
                  imageVector = Icons.Filled.ErrorOutline,
                  contentDescription = null,
                  tint = Color(0xFFF43F5E),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = errorMessage!!,
                  color = Color(0xFFFF8595),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Neon Sign-In Button
            Button(
              onClick = {
                if (studentId.isBlank()) {
                  errorMessage = "Please enter your UCC Student ID / Staff ID"
                } else if (email.isBlank() || !email.contains("@")) {
                  errorMessage = "Please enter a valid UCC email (@stu.ucc.edu.gh or @ucc.edu.gh)"
                } else {
                  val finalName = fullName.ifBlank {
                    if (selectedRole == UserRole.WORKER) "UCC Works Technician" else "UCC Student"
                  }
                  onSignIn(
                    studentId.trim(),
                    finalName.trim(),
                    email.trim(),
                    selectedRole,
                    department.trim()
                  )
                }
              },
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
              ),
              contentPadding = PaddingValues(),
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                  Brush.horizontalGradient(listOf(NeonGradientStart, NeonGradientEnd))
                )
                .testTag("signin_submit_button")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.Login,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Sign In to UCC CampusFix",
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      // 3. Quick 1-Tap Demo UCC Accounts
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Filled.FlashOn,
                contentDescription = null,
                tint = NeonAmber,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Instant 1-Tap UCC Test Profiles",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextNeonPrimary
              )
            }
            Text(
              text = "Tap any verified UCC account to sign in immediately:",
              fontSize = 11.sp,
              color = TextNeonTertiary,
              modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            // Demo 1: Kwame Mensah (Computer Science Student)
            DemoAccountRow(
              name = "Kwame Mensah",
              roleTag = "CS Student",
              id = "PS/CSC/21/0042",
              email = "kwame.mensah@stu.ucc.edu.gh",
              department = "Dept. of Computer Science & IT (UCC)",
              role = UserRole.STUDENT,
              badgeColor = NeonMagenta,
              onSelect = { sId, n, e, r, d ->
                studentId = sId
                fullName = n
                email = e
                selectedRole = r
                department = d
                onSignIn(sId, n, e, r, d)
              },
              testTag = "demo_signin_student"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Demo 2: Abena Asante (Education Student)
            DemoAccountRow(
              name = "Abena Asante",
              roleTag = "Education",
              id = "ED/EPS/22/0119",
              email = "abena.asante@stu.ucc.edu.gh",
              department = "Faculty of Educational Foundations (UCC)",
              role = UserRole.STUDENT,
              badgeColor = NeonCyan,
              onSelect = { sId, n, e, r, d ->
                studentId = sId
                fullName = n
                email = e
                selectedRole = r
                department = d
                onSignIn(sId, n, e, r, d)
              },
              testTag = "demo_signin_education"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Demo 3: Ing. Emmanuel Addo (Works & Physical Dev)
            DemoAccountRow(
              name = "Ing. Emmanuel Addo",
              roleTag = "Works Dept",
              id = "STAFF/0891",
              email = "e.addo@ucc.edu.gh",
              department = "Directorate of Physical Dev. & Estate Mgmt",
              role = UserRole.WORKER,
              badgeColor = UccGold,
              onSelect = { sId, n, e, r, d ->
                studentId = sId
                fullName = n
                email = e
                selectedRole = r
                department = d
                onSignIn(sId, n, e, r, d)
              },
              testTag = "demo_signin_works"
            )
          }
        }
      }

      // 4. UCC Features highlight banner
      item {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          FeaturePill(icon = Icons.Filled.QrCodeScanner, title = "QR Scans", desc = "Sandwich & NLT")
          FeaturePill(icon = Icons.Filled.FlashOn, title = "< 30 Secs", desc = "Fast Reporting")
          FeaturePill(icon = Icons.Filled.Engineering, title = "Direct Works", desc = "UCC Dispatch")
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

@Composable
private fun DemoAccountRow(
  name: String,
  roleTag: String,
  id: String,
  email: String,
  department: String,
  role: UserRole,
  badgeColor: Color,
  onSelect: (id: String, name: String, email: String, role: UserRole, department: String) -> Unit,
  testTag: String
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable {
        onSelect(id, name, email, role, department)
      }
      .testTag(testTag),
    color = DarkSurfaceElevated,
    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(badgeColor.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = when (role) {
              UserRole.STUDENT -> Icons.Filled.School
              UserRole.WORKER -> Icons.Filled.Engineering
            },
            contentDescription = null,
            tint = badgeColor,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = name,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = badgeColor.copy(alpha = 0.2f),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = roleTag,
                color = badgeColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
              )
            }
          }
          Text(
            text = "$id • $email",
            fontSize = 10.sp,
            color = TextNeonSecondary
          )
        }
      }

      Icon(
        imageVector = Icons.Filled.Login,
        contentDescription = "Sign in",
        tint = badgeColor,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

@Composable
private fun FeaturePill(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  desc: String
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(DarkSurfaceElevated)
        .border(1.dp, DarkBorder, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = NeonMagenta,
        modifier = Modifier.size(18.dp)
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = title,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = TextNeonPrimary
    )
    Text(
      text = desc,
      fontSize = 9.sp,
      color = TextNeonTertiary
    )
  }
}

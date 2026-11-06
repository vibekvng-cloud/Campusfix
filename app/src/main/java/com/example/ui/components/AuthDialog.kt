package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserRole
import com.example.ui.theme.SkillAlleyBg
import com.example.ui.theme.SkillAlleyBorder
import com.example.ui.theme.SkillAlleyButtonDark
import com.example.ui.theme.SkillAlleyCard
import com.example.ui.theme.SkillAlleyTextMuted
import com.example.ui.theme.SkillAlleyTextPrimary
import com.example.ui.theme.SkillAlleyTextSecondary
import com.example.ui.theme.UccGold

@Composable
fun AuthDialog(
  currentStudentId: String?,
  currentEmail: String?,
  currentName: String?,
  currentRole: UserRole,
  onDismiss: () -> Unit,
  onSignIn: (studentId: String, name: String, email: String, role: UserRole, department: String) -> Unit
) {
  var studentId by remember { mutableStateOf(currentStudentId ?: "") }
  var email by remember { mutableStateOf(currentEmail ?: "") }
  var fullName by remember { mutableStateOf(currentName ?: "") }
  var department by remember { mutableStateOf("Dept. of Computer Science & IT (UCC)") }
  var role by remember { mutableStateOf(currentRole) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("auth_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Update UCC ID",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = SkillAlleyTextPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = SkillAlleyButtonDark,
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "UCC",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "University of Cape Coast Credential",
              fontSize = 11.sp,
              color = SkillAlleyTextSecondary
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = SkillAlleyTextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Role Segmented Tabs
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          UserRole.entries.forEach { r ->
            val isSelected = r == role
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(100.dp))
                .clickable { role = r },
              shape = RoundedCornerShape(100.dp),
              color = if (isSelected) SkillAlleyButtonDark else SkillAlleyBg,
              border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
            ) {
              Text(
                text = r.title.split(" ").first(),
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else SkillAlleyTextPrimary,
                modifier = Modifier.padding(vertical = 7.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Full Name
        Text("Full Name", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SkillAlleyTextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = fullName,
          onValueChange = { fullName = it },
          placeholder = { Text("e.g. Kwame Mensah", fontSize = 12.sp, color = SkillAlleyTextMuted) },
          leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = SkillAlleyTextSecondary, modifier = Modifier.size(18.dp)) },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth().testTag("auth_dialog_name_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SkillAlleyButtonDark,
            unfocusedBorderColor = SkillAlleyBorder,
            focusedContainerColor = SkillAlleyCard,
            unfocusedContainerColor = SkillAlleyCard,
            focusedTextColor = SkillAlleyTextPrimary,
            unfocusedTextColor = SkillAlleyTextPrimary
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Student ID or Staff ID
        Text(
          if (role == UserRole.WORKER) "Staff Code / Employee ID" else "Student ID / Index Number",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = SkillAlleyTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = studentId,
          onValueChange = { studentId = it },
          placeholder = { Text(if (role == UserRole.WORKER) "STAFF/0891" else "PS/CSC/22/0199", fontSize = 12.sp, color = SkillAlleyTextMuted) },
          leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null, tint = SkillAlleyTextSecondary, modifier = Modifier.size(18.dp)) },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth().testTag("auth_dialog_student_id_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SkillAlleyButtonDark,
            unfocusedBorderColor = SkillAlleyBorder,
            focusedContainerColor = SkillAlleyCard,
            unfocusedContainerColor = SkillAlleyCard,
            focusedTextColor = SkillAlleyTextPrimary,
            unfocusedTextColor = SkillAlleyTextPrimary
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Email
        Text("Email Address", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SkillAlleyTextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          placeholder = { Text(if (role == UserRole.WORKER) "emmanuel.addo@gmail.com" else "kwame.mensah@stu.ucc.edu.gh", fontSize = 12.sp, color = SkillAlleyTextMuted) },
          leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = SkillAlleyTextSecondary, modifier = Modifier.size(18.dp)) },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth().testTag("auth_dialog_email_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SkillAlleyButtonDark,
            unfocusedBorderColor = SkillAlleyBorder,
            focusedContainerColor = SkillAlleyCard,
            unfocusedContainerColor = SkillAlleyCard,
            focusedTextColor = SkillAlleyTextPrimary,
            unfocusedTextColor = SkillAlleyTextPrimary
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Department
        Text("Faculty / Department", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SkillAlleyTextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = department,
          onValueChange = { department = it },
          placeholder = { Text("Dept. of Computer Science & IT", fontSize = 12.sp, color = SkillAlleyTextMuted) },
          leadingIcon = { Icon(Icons.Filled.School, contentDescription = null, tint = SkillAlleyTextSecondary, modifier = Modifier.size(18.dp)) },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth().testTag("auth_dialog_dept_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SkillAlleyButtonDark,
            unfocusedBorderColor = SkillAlleyBorder,
            focusedContainerColor = SkillAlleyCard,
            unfocusedContainerColor = SkillAlleyCard,
            focusedTextColor = SkillAlleyTextPrimary,
            unfocusedTextColor = SkillAlleyTextPrimary
          )
        )

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = errorMessage!!, color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            if (fullName.isBlank()) {
              errorMessage = "Please enter your full name."
              return@Button
            }
            if (studentId.isBlank()) {
              errorMessage = "Please enter your ID / Staff Code."
              return@Button
            }
            if (email.isBlank()) {
              errorMessage = "Please enter your email."
              return@Button
            }
            onSignIn(studentId.trim(), fullName.trim(), email.trim(), role, department.trim())
            onDismiss()
          },
          shape = RoundedCornerShape(100.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
          modifier = Modifier.fillMaxWidth().height(48.dp).testTag("auth_dialog_submit_btn")
        ) {
          Text("Save & Apply ->", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}

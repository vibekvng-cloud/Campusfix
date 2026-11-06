package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MaintenanceTeam
import com.example.data.model.UserRole
import com.example.ui.theme.SkillAlleyBadgeCircle
import com.example.ui.theme.SkillAlleyBg
import com.example.ui.theme.SkillAlleyBorder
import com.example.ui.theme.SkillAlleyBorderSubtle
import com.example.ui.theme.SkillAlleyButtonDark
import com.example.ui.theme.SkillAlleyCard
import com.example.ui.theme.SkillAlleyDarkCard
import com.example.ui.theme.SkillAlleyTextMuted
import com.example.ui.theme.SkillAlleyTextPrimary
import com.example.ui.theme.SkillAlleyTextSecondary
import com.example.ui.theme.UccGold

enum class AuthMode {
  LOGIN,
  SIGN_UP
}

enum class SignUpRoleType {
  STUDENT,
  WORKER_FACULTY
}

/**
 * Clean, modern User Authentication Screen matching the Skillalley visual reference:
 * - Crisp white card layout on soft neutral background
 * - Deep charcoal pill buttons with arrow indicator
 * - Distinct Login Screen (Email + Password + Login Button + switch link)
 * - Distinct Create Account Screen with separate "Student Sign Up" vs "UCC Worker / Faculty Sign Up"
 * - 1-Tap Quick Verified Demo Access for both Students and Workers
 */
@Composable
fun UccAuthScreen(
  onAuthenticate: (studentId: String, name: String, email: String, role: UserRole, department: String) -> Unit,
  initialMode: AuthMode = AuthMode.LOGIN,
  modifier: Modifier = Modifier
) {
  var currentMode by remember { mutableStateOf(initialMode) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(SkillAlleyBg)
      .statusBarsPadding()
      .testTag("ucc_auth_screen")
  ) {
    AnimatedContent(
      targetState = currentMode,
      transitionSpec = {
        if (targetState == AuthMode.SIGN_UP) {
          slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
        } else {
          slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
        }
      },
      label = "auth_screen_transition"
    ) { mode ->
      when (mode) {
        AuthMode.LOGIN -> {
          LoginScreenContent(
            onLogin = onAuthenticate,
            onSwitchToSignUp = { currentMode = AuthMode.SIGN_UP }
          )
        }
        AuthMode.SIGN_UP -> {
          SignUpScreenContent(
            onCreateAccount = onAuthenticate,
            onSwitchToLogin = { currentMode = AuthMode.LOGIN }
          )
        }
      }
    }
  }
}

/**
 * 1. SEPARATE LOGIN SCREEN
 * Student vs Works Dept login with exact typed Student ID / Staff Code preservation
 */
@Composable
private fun LoginScreenContent(
  onLogin: (studentId: String, name: String, email: String, role: UserRole, department: String) -> Unit,
  onSwitchToSignUp: () -> Unit
) {
  var loginRole by remember { mutableStateOf(UserRole.STUDENT) }
  var studentIdInput by remember { mutableStateOf("") }
  var studentEmailInput by remember { mutableStateOf("") }
  var workerCodeInput by remember { mutableStateOf("") }
  var workerEmailInput by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Spacer(modifier = Modifier.height(10.dp))
      AuthHeader(
        title = "Welcome Back.",
        subtitle = "Sign in with your UCC Student ID or Works Department credentials."
      )
    }

    // Main Login Card
    item {
      Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
        modifier = Modifier.fillMaxWidth().testTag("login_card")
      ) {
        Column(
          modifier = Modifier.padding(22.dp)
        ) {
          // Tab Toggle Header (Log In vs Create Account)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(SkillAlleyBg, RoundedCornerShape(12.dp))
              .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Surface(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              color = SkillAlleyButtonDark
            ) {
              Text(
                text = "Log In",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
              )
            }
            Surface(
              modifier = Modifier
                .weight(1f)
                .clickable { onSwitchToSignUp() }
                .testTag("switch_to_signup_tab"),
              shape = RoundedCornerShape(10.dp),
              color = Color.Transparent
            ) {
              Text(
                text = "Create Account",
                color = SkillAlleyTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Role Switcher: Student vs Works Dept
          Text(
            text = "Log in as",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = SkillAlleyTextPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(SkillAlleyBg, RoundedCornerShape(12.dp))
              .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            val isStudent = loginRole == UserRole.STUDENT
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  loginRole = UserRole.STUDENT
                  errorMessage = null
                }
                .testTag("login_tab_student"),
              shape = RoundedCornerShape(10.dp),
              color = if (isStudent) SkillAlleyButtonDark else Color.Transparent
            ) {
              Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  Icons.Filled.School,
                  contentDescription = null,
                  tint = if (isStudent) Color.White else SkillAlleyTextSecondary,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Student",
                  color = if (isStudent) Color.White else SkillAlleyTextSecondary,
                  fontSize = 12.sp,
                  fontWeight = if (isStudent) FontWeight.Bold else FontWeight.Medium
                )
              }
            }

            val isWorker = loginRole == UserRole.WORKER
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  loginRole = UserRole.WORKER
                  errorMessage = null
                }
                .testTag("login_tab_worker"),
              shape = RoundedCornerShape(10.dp),
              color = if (isWorker) SkillAlleyButtonDark else Color.Transparent
            ) {
              Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  Icons.Filled.Engineering,
                  contentDescription = null,
                  tint = if (isWorker) Color.White else SkillAlleyTextSecondary,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Works Dept",
                  color = if (isWorker) Color.White else SkillAlleyTextSecondary,
                  fontSize = 12.sp,
                  fontWeight = if (isWorker) FontWeight.Bold else FontWeight.Medium
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          if (loginRole == UserRole.STUDENT) {
            // Student ID field (Crucial: Exact ID is saved)
            Text(
              text = "UCC Student ID / Index Number",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = studentIdInput,
              onValueChange = {
                studentIdInput = it
                errorMessage = null
              },
              placeholder = { Text("e.g. PS/CSC/22/0199 or 20211100", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = {
                Icon(Icons.Filled.Badge, contentDescription = null, tint = SkillAlleyTextSecondary)
              },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("login_student_id_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Student School Email
            Text(
              text = "School Email (Optional or auto-generated)",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = studentEmailInput,
              onValueChange = {
                studentEmailInput = it
                errorMessage = null
              },
              placeholder = { Text("e.g. student@stu.ucc.edu.gh", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = {
                Icon(Icons.Filled.Mail, contentDescription = null, tint = SkillAlleyTextSecondary)
              },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("login_student_email_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )
          } else {
            // Worker Staff Code / Work ID
            Text(
              text = "Staff / Work Code",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = workerCodeInput,
              onValueChange = {
                workerCodeInput = it
                errorMessage = null
              },
              placeholder = { Text("e.g. STAFF/0891 or WORKS/ELEC/04", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = {
                Icon(Icons.Filled.Badge, contentDescription = null, tint = SkillAlleyTextSecondary)
              },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("login_worker_code_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Worker Gmail / Email
            Text(
              text = "Worker Gmail / Email",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = workerEmailInput,
              onValueChange = {
                workerEmailInput = it
                errorMessage = null
              },
              placeholder = { Text("e.g. works.ucc@gmail.com", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = {
                Icon(Icons.Filled.Mail, contentDescription = null, tint = SkillAlleyTextSecondary)
              },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("login_worker_email_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Password Field
          Text(
            text = "Password",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = SkillAlleyTextPrimary
          )
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = password,
            onValueChange = {
              password = it
              errorMessage = null
            },
            placeholder = { Text("Enter your password", color = SkillAlleyTextMuted, fontSize = 13.sp) },
            leadingIcon = {
              Icon(Icons.Filled.Lock, contentDescription = null, tint = SkillAlleyTextSecondary)
            },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                  contentDescription = if (passwordVisible) "Hide password" else "Show password",
                  tint = SkillAlleyTextSecondary
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_password_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = SkillAlleyButtonDark,
              unfocusedBorderColor = SkillAlleyBorder,
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color.White,
              focusedTextColor = SkillAlleyTextPrimary,
              unfocusedTextColor = SkillAlleyTextPrimary
            )
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
              Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(errorMessage!!, color = Color(0xFFB91C1C), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Skillalley Style Dark Pill Button with Arrow ->
          Button(
            onClick = {
              if (loginRole == UserRole.STUDENT) {
                val cleanStudentId = studentIdInput.trim()
                if (cleanStudentId.isBlank()) {
                  errorMessage = "Please enter your Student ID (e.g. PS/CSC/22/0199)"
                } else if (password.isBlank()) {
                  errorMessage = "Please enter your password"
                } else {
                  val finalEmail = studentEmailInput.trim().ifBlank {
                    val sanitized = cleanStudentId.lowercase().replace("/", ".")
                    "$sanitized@stu.ucc.edu.gh"
                  }
                  val studentName = if (finalEmail.contains("@")) {
                    finalEmail.substringBefore("@").replace(".", " ").split(" ")
                      .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
                  } else {
                    "UCC Student"
                  }
                  // CRITICAL: Preserve the exact student ID typed by the user!
                  onLogin(cleanStudentId, studentName, finalEmail, UserRole.STUDENT, "UCC Undergraduate Campus")
                }
              } else {
                val cleanStaffCode = workerCodeInput.trim()
                val cleanEmail = workerEmailInput.trim()
                if (cleanStaffCode.isBlank() && cleanEmail.isBlank()) {
                  errorMessage = "Please enter your Staff Code (e.g. STAFF/0891) or Gmail"
                } else if (password.isBlank()) {
                  errorMessage = "Please enter your password"
                } else {
                  val finalCode = cleanStaffCode.ifBlank { "STAFF/0891" }
                  val finalEmail = cleanEmail.ifBlank { "works.ucc@gmail.com" }
                  val workerName = if (finalEmail.contains("@")) {
                    finalEmail.substringBefore("@").replace(".", " ").split(" ")
                      .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
                  } else {
                    "UCC Works Technician"
                  }
                  onLogin(finalCode, workerName, finalEmail, UserRole.WORKER, "Directorate of Physical Development")
                }
              }
            },
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .shadow(elevation = 4.dp, shape = RoundedCornerShape(100.dp))
              .testTag("login_submit_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(
                text = if (loginRole == UserRole.STUDENT) "Log In as Student" else "Log In as Worker",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Switch link: "Don't have an account? Sign in & Create one"
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Don't have an account? ",
              fontSize = 12.sp,
              color = SkillAlleyTextSecondary
            )
            Text(
              text = "Sign up & Create one",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = SkillAlleyButtonDark,
              modifier = Modifier
                .clickable { onSwitchToSignUp() }
                .padding(4.dp)
                .testTag("link_to_signup")
            )
          }
        }
      }
    }

    // Quick 1-Tap Demo Logins (Both Student & Worker)
    item {
      QuickDemoAccountsCard(onSelect = onLogin)
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

/**
 * 2. SEPARATE SIGN UP / CREATE ACCOUNT SCREEN
 * Separate Student Sign Up vs UCC Worker / Faculty Sign Up (with Gmail support)
 */
@Composable
private fun SignUpScreenContent(
  onCreateAccount: (studentId: String, name: String, email: String, role: UserRole, department: String) -> Unit,
  onSwitchToLogin: () -> Unit
) {
  var signUpRoleType by remember { mutableStateOf(SignUpRoleType.STUDENT) }

  // Student Form fields
  var studentName by remember { mutableStateOf("") }
  var studentId by remember { mutableStateOf("") }
  var studentEmail by remember { mutableStateOf("") }
  var studentPassword by remember { mutableStateOf("") }
  var studentConfirmPassword by remember { mutableStateOf("") }

  // Worker / Faculty Form fields (Supports Gmail as requested!)
  var workerName by remember { mutableStateOf("") }
  var workerStaffCode by remember { mutableStateOf("") }
  var workerGmail by remember { mutableStateOf("") }
  var workerTeam by remember { mutableStateOf("Electrical Section") }
  var workerPassword by remember { mutableStateOf("") }
  var workerConfirmPassword by remember { mutableStateOf("") }

  var passwordVisible by remember { mutableStateOf(false) }
  var confirmPasswordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Spacer(modifier = Modifier.height(10.dp))
      AuthHeader(
        title = "Create Account.",
        subtitle = "Choose Student registration or UCC Works Department worker setup."
      )
    }

    item {
      Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
        modifier = Modifier.fillMaxWidth().testTag("signup_card")
      ) {
        Column(
          modifier = Modifier.padding(22.dp)
        ) {
          // Tab Toggle Header: Log In vs Create Account
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(SkillAlleyBg, RoundedCornerShape(12.dp))
              .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Surface(
              modifier = Modifier
                .weight(1f)
                .clickable { onSwitchToLogin() }
                .testTag("switch_to_login_tab"),
              shape = RoundedCornerShape(10.dp),
              color = Color.Transparent
            ) {
              Text(
                text = "Log In",
                color = SkillAlleyTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
              )
            }
            Surface(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              color = SkillAlleyButtonDark
            ) {
              Text(
                text = "Create Account",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // ROLE TYPE SELECTOR: Student vs UCC Worker / Faculty
          Text(
            text = "Select Account Category",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Option 1: Student
            Surface(
              modifier = Modifier
                .weight(1f)
                .clickable {
                  signUpRoleType = SignUpRoleType.STUDENT
                  errorMessage = null
                }
                .testTag("signup_select_student"),
              shape = RoundedCornerShape(14.dp),
              color = if (signUpRoleType == SignUpRoleType.STUDENT) SkillAlleyButtonDark else SkillAlleyBg,
              border = if (signUpRoleType == SignUpRoleType.STUDENT) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.School,
                  contentDescription = null,
                  tint = if (signUpRoleType == SignUpRoleType.STUDENT) Color.White else SkillAlleyTextSecondary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Student",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (signUpRoleType == SignUpRoleType.STUDENT) Color.White else SkillAlleyTextPrimary
                )
              }
            }

            // Option 2: UCC Worker / Faculty
            Surface(
              modifier = Modifier
                .weight(1f)
                .clickable {
                  signUpRoleType = SignUpRoleType.WORKER_FACULTY
                  errorMessage = null
                }
                .testTag("signup_select_worker"),
              shape = RoundedCornerShape(14.dp),
              color = if (signUpRoleType == SignUpRoleType.WORKER_FACULTY) SkillAlleyButtonDark else SkillAlleyBg,
              border = if (signUpRoleType == SignUpRoleType.WORKER_FACULTY) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.Engineering,
                  contentDescription = null,
                  tint = if (signUpRoleType == SignUpRoleType.WORKER_FACULTY) Color.White else SkillAlleyTextSecondary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "UCC Worker",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (signUpRoleType == SignUpRoleType.WORKER_FACULTY) Color.White else SkillAlleyTextPrimary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // DYNAMIC FORM BODY ACCORDING TO ROLE TYPE
          if (signUpRoleType == SignUpRoleType.STUDENT) {
            // STUDENT SIGN UP FIELDS
            Text(
              text = "Full Name",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = studentName,
              onValueChange = {
                studentName = it
                errorMessage = null
              },
              placeholder = { Text("e.g. Kwame Mensah", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = SkillAlleyTextSecondary) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("signup_student_name_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "UCC Student ID / Index Number",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = studentId,
              onValueChange = {
                studentId = it
                errorMessage = null
              },
              placeholder = { Text("e.g. PS/CSC/21/0042", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null, tint = SkillAlleyTextSecondary) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("signup_student_id_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Student Institutional Email",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = studentEmail,
              onValueChange = {
                studentEmail = it
                errorMessage = null
              },
              placeholder = { Text("e.g. kwame.mensah@stu.ucc.edu.gh", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = { Icon(Icons.Filled.Mail, contentDescription = null, tint = SkillAlleyTextSecondary) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("signup_student_email_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Password",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = studentPassword,
              onValueChange = {
                studentPassword = it
                errorMessage = null
              },
              placeholder = { Text("Create password (min 4 chars)", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = SkillAlleyTextSecondary) },
              visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                  Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, tint = SkillAlleyTextSecondary)
                }
              },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("signup_student_password_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Confirm Password",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = studentConfirmPassword,
              onValueChange = {
                studentConfirmPassword = it
                errorMessage = null
              },
              placeholder = { Text("Re-enter password", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = SkillAlleyTextSecondary) },
              visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                  Icon(if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, tint = SkillAlleyTextSecondary)
                }
              },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("signup_student_confirm_password_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )

          } else {
            // UCC WORKER / FACULTY SIGN UP FIELDS (Allows Gmail as requested!)
            Text(
              text = "Worker Full Name & Title",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = workerName,
              onValueChange = {
                workerName = it
                errorMessage = null
              },
              placeholder = { Text("e.g. Ing. Emmanuel Addo or Kofi Mensah", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = SkillAlleyTextSecondary) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("signup_worker_name_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Worker Email (Gmail or UCC Email)",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = workerGmail,
              onValueChange = {
                workerGmail = it
                errorMessage = null
              },
              placeholder = { Text("e.g. emmanuel.addo.ucc@gmail.com or e.addo@ucc.edu.gh", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = { Icon(Icons.Filled.Mail, contentDescription = null, tint = SkillAlleyTextSecondary) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("signup_worker_email_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Works Section / Trade Team",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              val teams = listOf(
                "Electrical Section",
                "Carpentry & Desks",
                "Plumbing & Washrooms",
                "ICT & Projectors AV",
                "Works Directorate"
              )
              items(teams) { team ->
                val isPicked = workerTeam == team
                Surface(
                  modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .clickable { workerTeam = team },
                  shape = RoundedCornerShape(100.dp),
                  color = if (isPicked) SkillAlleyButtonDark else SkillAlleyBg,
                  border = if (isPicked) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
                ) {
                  Text(
                    text = team,
                    fontSize = 11.sp,
                    fontWeight = if (isPicked) FontWeight.Bold else FontWeight.Medium,
                    color = if (isPicked) Color.White else SkillAlleyTextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Staff / Work Code",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = workerStaffCode,
              onValueChange = {
                workerStaffCode = it
                errorMessage = null
              },
              placeholder = { Text("e.g. STAFF/0891 or WORKS/ELEC/04", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null, tint = SkillAlleyTextSecondary) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("signup_worker_code_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Password",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = workerPassword,
              onValueChange = {
                workerPassword = it
                errorMessage = null
              },
              placeholder = { Text("Create password", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = SkillAlleyTextSecondary) },
              visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                  Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, tint = SkillAlleyTextSecondary)
                }
              },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("signup_worker_password_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Confirm Password",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = workerConfirmPassword,
              onValueChange = {
                workerConfirmPassword = it
                errorMessage = null
              },
              placeholder = { Text("Re-enter password", color = SkillAlleyTextMuted, fontSize = 13.sp) },
              leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = SkillAlleyTextSecondary) },
              visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                  Icon(if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, tint = SkillAlleyTextSecondary)
                }
              },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("signup_worker_confirm_password_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkillAlleyButtonDark,
                unfocusedBorderColor = SkillAlleyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = SkillAlleyTextPrimary,
                unfocusedTextColor = SkillAlleyTextPrimary
              )
            )
          }

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
              Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(errorMessage!!, color = Color(0xFFB91C1C), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Primary Dark Pill Create Account Button with Arrow ->
          Button(
            onClick = {
              if (signUpRoleType == SignUpRoleType.STUDENT) {
                val cleanStudentId = studentId.trim()
                if (cleanStudentId.isBlank()) {
                  errorMessage = "Please enter your Student ID (e.g. PS/CSC/22/0199)"
                } else if (studentEmail.isBlank() || !studentEmail.contains("@")) {
                  errorMessage = "Please enter a valid student email"
                } else if (studentPassword.length < 4) {
                  errorMessage = "Password must be at least 4 characters"
                } else if (studentPassword != studentConfirmPassword) {
                  errorMessage = "Passwords do not match. Please re-enter."
                } else {
                  val finalName = studentName.trim().ifBlank { "UCC Student" }
                  // CRITICAL: Preserve exact typed student ID!
                  onCreateAccount(cleanStudentId, finalName, studentEmail.trim(), UserRole.STUDENT, "UCC Undergraduate Campus")
                }
              } else {
                val cleanCode = workerStaffCode.trim()
                if (cleanCode.isBlank()) {
                  errorMessage = "Please enter your Staff / Work Code (e.g. STAFF/0891)"
                } else if (workerGmail.isBlank() || !workerGmail.contains("@")) {
                  errorMessage = "Please enter a valid email or Gmail address"
                } else if (workerPassword.length < 4) {
                  errorMessage = "Password must be at least 4 characters"
                } else if (workerPassword != workerConfirmPassword) {
                  errorMessage = "Passwords do not match. Please re-enter."
                } else {
                  val finalName = workerName.trim().ifBlank { "UCC Works Technician" }
                  onCreateAccount(cleanCode, finalName, workerGmail.trim(), UserRole.WORKER, workerTeam)
                }
              }
            },
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .shadow(elevation = 4.dp, shape = RoundedCornerShape(100.dp))
              .testTag("signup_submit_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(
                text = if (signUpRoleType == SignUpRoleType.STUDENT) "Create Student Account" else "Create Worker Account",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Switch link: "Already have an account? Log In"
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Already have an account? ",
              fontSize = 12.sp,
              color = SkillAlleyTextSecondary
            )
            Text(
              text = "Log In",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = SkillAlleyButtonDark,
              modifier = Modifier
                .clickable { onSwitchToLogin() }
                .padding(4.dp)
                .testTag("link_to_login")
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
private fun AuthHeader(
  title: String,
  subtitle: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth()
  ) {
    // UCC Emblem Pill
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .background(SkillAlleyCard, RoundedCornerShape(100.dp))
        .border(1.dp, SkillAlleyBorder, RoundedCornerShape(100.dp))
        .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(22.dp)
          .clip(CircleShape)
          .background(UccGold.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Filled.School, contentDescription = null, tint = UccGold, modifier = Modifier.size(14.dp))
      }
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "UNIVERSITY OF CAPE COAST",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = SkillAlleyTextPrimary,
        letterSpacing = 0.8.sp
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = title,
      fontSize = 28.sp,
      fontWeight = FontWeight.ExtraBold,
      color = SkillAlleyTextPrimary,
      letterSpacing = (-0.5).sp
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = subtitle,
      fontSize = 13.sp,
      color = SkillAlleyTextSecondary,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 16.dp)
    )
  }
}

@Composable
private fun QuickDemoAccountsCard(
  onSelect: (id: String, name: String, email: String, role: UserRole, department: String) -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(SkillAlleyBadgeCircle)
            .border(1.dp, SkillAlleyBorder, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Filled.FlashOn, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text("Quick 1-Tap Demo Access", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SkillAlleyTextPrimary)
          Text("Instant login as Student or Works Dept Worker:", fontSize = 11.sp, color = SkillAlleyTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 1. Student Demo Account
      DemoAccountRowSkillalley(
        name = "Kwame Mensah",
        roleTag = "Student",
        id = "PS/CSC/21/0042",
        email = "kwame.mensah@stu.ucc.edu.gh",
        department = "Dept. of Computer Science & IT (UCC)",
        role = UserRole.STUDENT,
        scoreTag = "Student",
        onSelect = onSelect,
        testTag = "demo_login_student"
      )

      Spacer(modifier = Modifier.height(8.dp))

      // 2. UCC Works Department Worker Demo Account
      DemoAccountRowSkillalley(
        name = "Ing. Emmanuel Addo",
        roleTag = "Works Dept Worker",
        id = "STAFF/0891",
        email = "emmanuel.addo.ucc@gmail.com",
        department = "Electrical & Works Operations",
        role = UserRole.WORKER,
        scoreTag = "Worker",
        onSelect = onSelect,
        testTag = "demo_login_works"
      )

      Spacer(modifier = Modifier.height(8.dp))

      // 3. Second Student Demo
      DemoAccountRowSkillalley(
        name = "Abena Asante",
        roleTag = "Student",
        id = "ED/EPS/22/0119",
        email = "abena.asante@stu.ucc.edu.gh",
        department = "Faculty of Educational Foundations",
        role = UserRole.STUDENT,
        scoreTag = "Student",
        onSelect = onSelect,
        testTag = "demo_login_education"
      )

      Spacer(modifier = Modifier.height(8.dp))

      // 4. Second Worker Demo (Plumbing Technician)
      DemoAccountRowSkillalley(
        name = "Kofi Mensah",
        roleTag = "Plumbing Technician",
        id = "STAFF/0512",
        email = "kofi.mensah.ucc@gmail.com",
        department = "Plumbing & Washrooms Team",
        role = UserRole.WORKER,
        scoreTag = "Worker",
        onSelect = onSelect,
        testTag = "demo_login_plumber"
      )
    }
  }
}

@Composable
private fun DemoAccountRowSkillalley(
  name: String,
  roleTag: String,
  id: String,
  email: String,
  department: String,
  role: UserRole,
  scoreTag: String,
  onSelect: (id: String, name: String, email: String, role: UserRole, department: String) -> Unit,
  testTag: String
) {
  val isWorker = role == UserRole.WORKER

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable { onSelect(id, name, email, role, department) }
      .testTag(testTag),
    color = SkillAlleyBg,
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorderSubtle)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (isWorker) Color(0xFFE0E7FF) else Color(0xFFFEF3C7))
            .border(1.dp, SkillAlleyBorder, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isWorker) Icons.Filled.Engineering else Icons.Filled.School,
            contentDescription = null,
            tint = if (isWorker) Color(0xFF4338CA) else Color(0xFFB45309),
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SkillAlleyTextPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = SkillAlleyCard,
              shape = RoundedCornerShape(6.dp),
              border = androidx.compose.foundation.BorderStroke(0.8.dp, SkillAlleyBorder)
            ) {
              Text(
                text = roleTag,
                color = SkillAlleyTextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text("$id • $email", fontSize = 10.sp, color = SkillAlleyTextMuted)
        }
      }

      // Role Indicator Pill (Student vs Worker)
      Surface(
        color = if (isWorker) SkillAlleyButtonDark else SkillAlleyCard,
        shape = RoundedCornerShape(100.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
        modifier = Modifier.padding(start = 6.dp)
      ) {
        Text(
          text = scoreTag,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = if (isWorker) Color.White else SkillAlleyTextPrimary,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
      }
    }
  }
}

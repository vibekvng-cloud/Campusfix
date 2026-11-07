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
import androidx.compose.material.icons.filled.AdminPanelSettings
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
  WORKER
}

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
 * 1. LOGIN SCREEN
 * Student, Works Dept Staff, Admin & Guest Login with exact Registration/Index number preservation
 */
@Composable
private fun LoginScreenContent(
  onLogin: (studentId: String, name: String, email: String, role: UserRole, department: String) -> Unit,
  onSwitchToSignUp: () -> Unit
) {
  var loginRole by remember { mutableStateOf(UserRole.STUDENT) }
  var registrationOrIndexInput by remember { mutableStateOf("") }
  var studentEmailInput by remember { mutableStateOf("") }
  var workerCodeInput by remember { mutableStateOf("") }
  var workerEmailInput by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var guestAliasInput by remember { mutableStateOf("") }
  var showAdminModal by remember { mutableStateOf(false) }
  var adminModalEmail by remember { mutableStateOf("admin@gmail.com") }
  var adminModalPassword by remember { mutableStateOf("") }
  var adminModalError by remember { mutableStateOf<String?>(null) }

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
        subtitle = "Sign in to access UCC Student or Works Department dashboard."
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

          // 2-way Role Switcher: Student vs Works Dept (Admin is hidden from public view)
          Text(
            text = "Select Account Role",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = SkillAlleyTextPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(SkillAlleyBg, RoundedCornerShape(12.dp))
              .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            // Student
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

            // Works Dept Staff
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
                  text = "Works Staff",
                  color = if (isWorker) Color.White else SkillAlleyTextSecondary,
                  fontSize = 12.sp,
                  fontWeight = if (isWorker) FontWeight.Bold else FontWeight.Medium
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Form fields based on selected role
          when (loginRole) {
            UserRole.STUDENT -> {
              // Registration Number or Index Number
              Text(
                text = "Registration Number or Index Number",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SkillAlleyTextPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = registrationOrIndexInput,
                onValueChange = {
                  registrationOrIndexInput = it
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
            }
            UserRole.WORKER -> {
              // Staff / Work Code
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
                text = "Worker Email (Gmail or UCC Email)",
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
            else -> {}
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

          // Primary Dark Pill Button with Arrow ->
          Button(
            onClick = {
              when (loginRole) {
                UserRole.STUDENT -> {
                  val cleanReg = registrationOrIndexInput.trim()
                  val cleanEmail = studentEmailInput.trim()

                  // Check if admin is logging in via the primary login inputs
                  if (cleanReg.equals("admin@gmail.com", ignoreCase = true) ||
                      cleanEmail.equals("admin@gmail.com", ignoreCase = true) ||
                      cleanReg.equals("ADMIN/001", ignoreCase = true)
                  ) {
                    if (password == "1234") {
                      onLogin("ADMIN/001", "Prof. K. Baidoo", "admin@gmail.com", UserRole.ADMIN, "Directorate of Physical Development & Works")
                    } else {
                      errorMessage = "Invalid admin password. Please try again."
                    }
                  } else if (cleanReg.isBlank()) {
                    errorMessage = "Please enter your Registration Number or Index Number (e.g. PS/CSC/22/0199)"
                  } else if (password.isBlank()) {
                    errorMessage = "Please enter your password"
                  } else {
                    val finalEmail = cleanEmail.ifBlank {
                      val sanitized = cleanReg.lowercase().replace("/", ".")
                      "$sanitized@stu.ucc.edu.gh"
                    }
                    val studentName = if (finalEmail.contains("@")) {
                      finalEmail.substringBefore("@").replace(".", " ").split(" ")
                        .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
                    } else {
                      "UCC Student"
                    }
                    onLogin(cleanReg, studentName, finalEmail, UserRole.STUDENT, "UCC Undergraduate Campus")
                  }
                }
                UserRole.WORKER -> {
                  val cleanStaffCode = workerCodeInput.trim()
                  val cleanEmail = workerEmailInput.trim()

                  // Check if admin is logging in via worker input
                  if (cleanStaffCode.equals("admin@gmail.com", ignoreCase = true) ||
                      cleanEmail.equals("admin@gmail.com", ignoreCase = true) ||
                      cleanStaffCode.equals("ADMIN/001", ignoreCase = true)
                  ) {
                    if (password == "1234") {
                      onLogin("ADMIN/001", "Prof. K. Baidoo", "admin@gmail.com", UserRole.ADMIN, "Directorate of Physical Development & Works")
                    } else {
                      errorMessage = "Invalid admin password. Please try again."
                    }
                  } else if (cleanStaffCode.isBlank() && cleanEmail.isBlank()) {
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
                    onLogin(finalCode, workerName, finalEmail, UserRole.WORKER, "Works Directorate (Staff)")
                  }
                }
                else -> {}
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
                text = when (loginRole) {
                  UserRole.STUDENT -> "Log In as Student"
                  UserRole.WORKER -> "Log In as Staff (Works Dept)"
                  else -> "Log In"
                },
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

          // Switch link
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

          Spacer(modifier = Modifier.height(10.dp))

          // Discrete Directorate Portal Login (Not visible as normal role tabs)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(100.dp),
              color = Color.Transparent,
              modifier = Modifier
                .clickable {
                  adminModalError = null
                  adminModalPassword = ""
                  showAdminModal = true
                }
                .testTag("directorate_portal_link")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Icon(
                  Icons.Filled.Lock,
                  contentDescription = null,
                  tint = SkillAlleyTextMuted,
                  modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Directorate Portal",
                  fontSize = 11.sp,
                  color = SkillAlleyTextMuted,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }
    }

    // CAMPUS GUEST CARD: Sign in with just an alias (No password needed)
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
        modifier = Modifier.fillMaxWidth().testTag("guest_login_card")
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
                  .background(Color(0xFFECFDF5))
                  .border(1.dp, Color(0xFFA7F3D0), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(18.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Campus Guest Access", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SkillAlleyTextPrimary)
                Text("Not a student or staff? Sign in with an alias", fontSize = 11.sp, color = SkillAlleyTextSecondary)
              }
            }

            Surface(
              color = Color(0xFFECFDF5),
              shape = RoundedCornerShape(100.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
            ) {
              Text(
                text = "Guest Pass",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF047857),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text("Enter Your Preferred Alias or Name", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SkillAlleyTextPrimary)
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = guestAliasInput,
            onValueChange = { guestAliasInput = it },
            placeholder = { Text("e.g. Visitor Kojo or Guest Kwame", color = SkillAlleyTextMuted, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null, tint = SkillAlleyTextSecondary, modifier = Modifier.size(16.dp)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("guest_alias_input"),
            shape = RoundedCornerShape(12.dp),
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

          Button(
            onClick = {
              val cleanAlias = guestAliasInput.trim().ifBlank { "Campus Visitor" }
              val guestId = "GUEST/${(1000..9999).random()}"
              val email = "${cleanAlias.lowercase().replace(" ", ".")}@guest.ucc.edu.gh"
              onLogin(guestId, cleanAlias, email, UserRole.GUEST, "Campus Guest Privileges")
            },
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyButtonDark),
            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("guest_enter_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("Continue as Guest with Alias", color = SkillAlleyButtonDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(6.dp))
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = SkillAlleyButtonDark, modifier = Modifier.size(14.dp))
            }
          }
        }
      }
    }

    // Quick 1-Tap Demo Logins (Student, Staff, Admin, Guest)
    item {
      QuickDemoAccountsCard(onSelect = onLogin)
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  if (showAdminModal) {
    androidx.compose.material3.AlertDialog(
      onDismissRequest = { showAdminModal = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = SkillAlleyButtonDark)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Directorate Secure Login",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Restricted Directorate & Works Administration portal. Only authorized administrators may sign in.",
            fontSize = 12.sp,
            color = SkillAlleyTextSecondary
          )

          OutlinedTextField(
            value = adminModalEmail,
            onValueChange = {
              adminModalEmail = it
              adminModalError = null
            },
            label = { Text("Admin Email", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("modal_admin_email_input"),
            shape = RoundedCornerShape(12.dp)
          )

          OutlinedTextField(
            value = adminModalPassword,
            onValueChange = {
              adminModalPassword = it
              adminModalError = null
            },
            label = { Text("Password", fontSize = 12.sp) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("modal_admin_password_input"),
            shape = RoundedCornerShape(12.dp)
          )

          if (adminModalError != null) {
            Text(
              text = adminModalError!!,
              color = Color(0xFFDC2626),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val cleanEmail = adminModalEmail.trim()
            if (cleanEmail.equals("admin@gmail.com", ignoreCase = true) && adminModalPassword == "1234") {
              showAdminModal = false
              onLogin("ADMIN/001", "Prof. K. Baidoo", "admin@gmail.com", UserRole.ADMIN, "Directorate of Physical Development & Works")
            } else if (!cleanEmail.equals("admin@gmail.com", ignoreCase = true)) {
              adminModalError = "Invalid admin email. Expected admin@gmail.com"
            } else {
              adminModalError = "Invalid admin password (Hint: 1234)"
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
          shape = RoundedCornerShape(100.dp),
          modifier = Modifier.testTag("modal_admin_submit")
        ) {
          Text("Access Admin Portal", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        androidx.compose.material3.TextButton(onClick = { showAdminModal = false }) {
          Text("Cancel", color = SkillAlleyTextSecondary)
        }
      },
      containerColor = Color.White,
      shape = RoundedCornerShape(20.dp)
    )
  }
}

/**
 * 2. CREATE ACCOUNT SCREEN
 * Separate Student Sign Up, Works Dept Staff Sign Up & Directorate Admin Sign Up
 */
@Composable
private fun SignUpScreenContent(
  onCreateAccount: (studentId: String, name: String, email: String, role: UserRole, department: String) -> Unit,
  onSwitchToLogin: () -> Unit
) {
  var signUpRoleType by remember { mutableStateOf(SignUpRoleType.STUDENT) }

  // Student fields
  var studentName by remember { mutableStateOf("") }
  var registrationOrIndexNumber by remember { mutableStateOf("") }
  var studentEmail by remember { mutableStateOf("") }
  var studentPassword by remember { mutableStateOf("") }
  var studentConfirmPassword by remember { mutableStateOf("") }

  // Worker fields
  var workerName by remember { mutableStateOf("") }
  var workerStaffCode by remember { mutableStateOf("") }
  var workerGmail by remember { mutableStateOf("") }
  var workerTeam by remember { mutableStateOf("Electrical Operations") }
  var workerPassword by remember { mutableStateOf("") }
  var workerConfirmPassword by remember { mutableStateOf("") }

  // Guest Alias for quick jump
  var guestAlias by remember { mutableStateOf("") }

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
        title = "Create UCC Account",
        subtitle = "Register as a UCC Student or Works Department Staff."
      )
    }

    // Main Registration Card
    item {
      Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
        modifier = Modifier.fillMaxWidth().testTag("signup_card")
      ) {
        Column(modifier = Modifier.padding(22.dp)) {
          // Tab Toggle Header (Log In vs Create Account)
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

          // 2-way Role Selection: Student or Staff (Works Dept) - Admin can only login
          Text(
            text = "Select Account Type",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = SkillAlleyTextPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Student
            Surface(
              modifier = Modifier
                .weight(1f)
                .clickable {
                  signUpRoleType = SignUpRoleType.STUDENT
                  errorMessage = null
                }
                .testTag("signup_select_student"),
              shape = RoundedCornerShape(12.dp),
              color = if (signUpRoleType == SignUpRoleType.STUDENT) SkillAlleyButtonDark else SkillAlleyBg,
              border = if (signUpRoleType == SignUpRoleType.STUDENT) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.School,
                  contentDescription = null,
                  tint = if (signUpRoleType == SignUpRoleType.STUDENT) Color.White else SkillAlleyTextSecondary,
                  modifier = Modifier.size(15.dp)
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

            // Staff (Works Dept)
            Surface(
              modifier = Modifier
                .weight(1f)
                .clickable {
                  signUpRoleType = SignUpRoleType.WORKER
                  errorMessage = null
                }
                .testTag("signup_select_worker"),
              shape = RoundedCornerShape(12.dp),
              color = if (signUpRoleType == SignUpRoleType.WORKER) SkillAlleyButtonDark else SkillAlleyBg,
              border = if (signUpRoleType == SignUpRoleType.WORKER) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.Engineering,
                  contentDescription = null,
                  tint = if (signUpRoleType == SignUpRoleType.WORKER) Color.White else SkillAlleyTextSecondary,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Works Staff",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (signUpRoleType == SignUpRoleType.WORKER) Color.White else SkillAlleyTextPrimary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // DYNAMIC FORM BODY ACCORDING TO ROLE TYPE
          when (signUpRoleType) {
            SignUpRoleType.STUDENT -> {
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
                text = "Registration Number or Index Number",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SkillAlleyTextPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = registrationOrIndexNumber,
                onValueChange = {
                  registrationOrIndexNumber = it
                  errorMessage = null
                },
                placeholder = { Text("e.g. PS/CSC/22/0199 or 20211100", color = SkillAlleyTextMuted, fontSize = 13.sp) },
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
            }
            SignUpRoleType.WORKER -> {
              // UCC WORKER SIGN UP FIELDS
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
                text = "Assigned Trade / Section",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SkillAlleyTextPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                val teams = listOf(
                  "Electrical",
                  "Plumbing",
                  "IT & AV",
                  "Carpentry",
                  "Cooling & AC"
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
              when (signUpRoleType) {
                SignUpRoleType.STUDENT -> {
                  val cleanReg = registrationOrIndexNumber.trim()
                  if (cleanReg.isBlank()) {
                    errorMessage = "Please enter your Registration Number or Index Number"
                  } else if (studentEmail.isBlank() || !studentEmail.contains("@")) {
                    errorMessage = "Please enter a valid student institutional email"
                  } else if (studentPassword.length < 4) {
                    errorMessage = "Password must be at least 4 characters"
                  } else if (studentPassword != studentConfirmPassword) {
                    errorMessage = "Passwords do not match. Please re-enter."
                  } else {
                    val finalName = studentName.trim().ifBlank { "UCC Student" }
                    onCreateAccount(cleanReg, finalName, studentEmail.trim(), UserRole.STUDENT, "UCC Undergraduate Campus")
                  }
                }
                SignUpRoleType.WORKER -> {
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
                text = when (signUpRoleType) {
                  SignUpRoleType.STUDENT -> "Create Student Account"
                  SignUpRoleType.WORKER -> "Create Staff (Works) Account"
                },
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

          // Switch link
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

    // Quick Guest shortcut
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Need Instant Guest Access?",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "You can jump right in as a visitor using just a screen alias.",
            fontSize = 11.sp,
            color = SkillAlleyTextSecondary
          )
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = {
              val guestId = "GUEST/${(1000..9999).random()}"
              onCreateAccount(guestId, "Campus Visitor", "visitor@guest.ucc.edu.gh", UserRole.GUEST, "Campus Guest Access")
            },
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyButtonDark),
            modifier = Modifier.fillMaxWidth().height(42.dp)
          ) {
            Text("Join as Campus Guest ->", color = SkillAlleyButtonDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Surface(
      color = SkillAlleyCard,
      shape = RoundedCornerShape(100.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(UccGold)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "UNIVERSITY OF CAPE COAST • CAMPUSFIX",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp,
          color = SkillAlleyTextPrimary
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = title,
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = SkillAlleyTextPrimary,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(4.dp))

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
          Text("Instant login into Student, Staff, or Guest dashboards:", fontSize = 11.sp, color = SkillAlleyTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 1. Student Demo Account (Registration / Index Number)
      DemoAccountRowSkillalley(
        name = "Kwame Mensah",
        roleTag = "Student",
        id = "PS/CSC/22/0199",
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
        roleTag = "Staff Worker",
        id = "STAFF/0891",
        email = "emmanuel.addo.ucc@gmail.com",
        department = "Electrical & Works Operations",
        role = UserRole.WORKER,
        scoreTag = "Staff",
        onSelect = onSelect,
        testTag = "demo_login_works"
      )

      Spacer(modifier = Modifier.height(8.dp))

      // 4. Campus Guest Demo Account
      DemoAccountRowSkillalley(
        name = "Kojo Visitor",
        roleTag = "Campus Guest",
        id = "GUEST/7721",
        email = "kojo.visitor@guest.ucc.edu.gh",
        department = "Campus Guest Privileges",
        role = UserRole.GUEST,
        scoreTag = "Guest",
        onSelect = onSelect,
        testTag = "demo_login_guest"
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
  val (avatarBg, iconTint, icon) = when (role) {
    UserRole.STUDENT -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), Icons.Filled.School)
    UserRole.WORKER -> Triple(Color(0xFFE0E7FF), Color(0xFF4338CA), Icons.Filled.Engineering)
    UserRole.ADMIN -> Triple(Color(0xFFF3E8FF), Color(0xFF7E22CE), Icons.Filled.AdminPanelSettings)
    UserRole.GUEST -> Triple(Color(0xFFECFDF5), Color(0xFF047857), Icons.Filled.Person)
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable { onSelect(id, name, email, role, department) }
      .testTag(testTag),
    color = SkillAlleyBg,
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
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
            .background(avatarBg)
            .border(1.dp, SkillAlleyBorder, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
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

      Surface(
        color = if (role == UserRole.ADMIN || role == UserRole.WORKER) SkillAlleyButtonDark else SkillAlleyCard,
        shape = RoundedCornerShape(100.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
        modifier = Modifier.padding(start = 6.dp)
      ) {
        Text(
          text = scoreTag,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = if (role == UserRole.ADMIN || role == UserRole.WORKER) Color.White else SkillAlleyTextPrimary,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
      }
    }
  }
}

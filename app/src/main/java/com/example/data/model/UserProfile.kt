package com.example.data.model

enum class UserRole(val title: String, val badge: String) {
  STUDENT("UCC Student", "Student"),
  WORKER("UCC Works Dept. (Staff)", "Staff Worker"),
  ADMIN("Directorate Admin", "Administrator"),
  GUEST("Campus Guest", "Guest");

  val isWorker: Boolean get() = this == WORKER
  val isAdmin: Boolean get() = this == ADMIN
  val isGuest: Boolean get() = this == GUEST
  val isStudent: Boolean get() = this == STUDENT
  val canViewStudentDashboard: Boolean get() = this == STUDENT || this == GUEST || this == ADMIN
  val canViewStaffDashboard: Boolean get() = this == WORKER || this == ADMIN
}

data class UserProfile(
  val id: String,
  val studentOrStaffId: String, // Registration/Index Number (e.g. PS/CSC/22/0199 or 20211100), Staff Code, Admin Code, or Guest Alias
  val fullName: String,
  val email: String, // e.g. kwame.mensah@stu.ucc.edu.gh, works.ucc@gmail.com, admin@ucc.edu.gh, or guest alias
  val role: UserRole,
  val department: String, // e.g. Dept. of Computer Science & IT, Electrical Section, Directorate Admin, or Visitor
  val hallOfResidence: String = "Casely Hayford Hall",
  val phone: String = "+233 24 555 8891",
  val avatarSeed: String = "UccUser"
)

data class OnlineWorker(
  val id: String,
  val name: String,
  val staffCode: String,
  val team: String, // Electrical, Plumbing, IT & AV, Carpentry, Cooling & AC
  val isOnline: Boolean = true,
  val activeJobsCount: Int = 0,
  val avatarInitials: String = "W"
)

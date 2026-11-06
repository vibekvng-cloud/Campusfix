package com.example.data.model

enum class UserRole(val title: String, val badge: String) {
  STUDENT("UCC Student", "Student"),
  WORKER("UCC Works Dept.", "Worker");

  val isWorker: Boolean get() = this == WORKER
}

data class UserProfile(
  val id: String,
  val studentOrStaffId: String, // e.g. PS/CSC/21/0042 or user's typed ID
  val fullName: String,
  val email: String, // e.g. kwame.mensah@stu.ucc.edu.gh or gmail
  val role: UserRole,
  val department: String, // e.g. Dept. of Computer Science & IT or Electrical Section
  val hallOfResidence: String = "Casely Hayford Hall",
  val phone: String = "+233 24 555 8891",
  val avatarSeed: String = "UccUser"
)

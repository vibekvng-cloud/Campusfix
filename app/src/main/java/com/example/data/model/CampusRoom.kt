package com.example.data.model

data class CampusRoom(
  val roomCode: String,
  val roomName: String,
  val building: String,
  val floor: String,
  val capacity: Int,
  val commonEquipment: List<String>,
  val qrPayload: String, // e.g. "CAMPUSFIX://room/LH-101"
  val recurringFaultCount: Int = 0
)

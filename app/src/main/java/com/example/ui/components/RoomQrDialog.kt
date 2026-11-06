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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CampusRoom
import com.example.ui.theme.SkillAlleyBg
import com.example.ui.theme.SkillAlleyBorder
import com.example.ui.theme.SkillAlleyButtonDark
import com.example.ui.theme.SkillAlleyCard
import com.example.ui.theme.SkillAlleyTextMuted
import com.example.ui.theme.SkillAlleyTextPrimary
import com.example.ui.theme.SkillAlleyTextSecondary
import com.example.ui.theme.UccGold

@Composable
fun RoomQrDialog(
  initialRoom: CampusRoom,
  allRooms: List<CampusRoom>,
  onDismiss: () -> Unit,
  onScanThisRoom: (CampusRoom) -> Unit
) {
  var selectedRoom by remember { mutableStateOf(initialRoom) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("room_qr_dialog")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                text = "UCC Theatre Placard",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SkillAlleyTextPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = SkillAlleyButtonDark,
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "OFFICIAL",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "University of Cape Coast QR sticker",
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

        // Switch Theatre Bar
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(allRooms) { room ->
            val isSelected = room.roomCode == selectedRoom.roomCode
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .clickable { selectedRoom = room },
              shape = RoundedCornerShape(100.dp),
              color = if (isSelected) SkillAlleyButtonDark else SkillAlleyBg,
              border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
            ) {
              Text(
                text = room.roomCode,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else SkillAlleyTextPrimary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Placard Frame
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Color.White,
          border = androidx.compose.foundation.BorderStroke(1.5.dp, SkillAlleyBorder),
          shadowElevation = 2.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Filled.School,
                contentDescription = null,
                tint = SkillAlleyButtonDark,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "UNIVERSITY OF CAPE COAST",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SkillAlleyButtonDark,
                letterSpacing = 1.sp
              )
            }
            Text(
              text = "CAMPUSFIX • SMART REPORTING",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = SkillAlleyTextSecondary,
              letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // The Canvas QR Code
            CampusQrCodeCanvas(
              payload = selectedRoom.qrPayload,
              size = 150.dp,
              dotColor = Color(0xFF09070F),
              backgroundColor = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = selectedRoom.roomName,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF09070F),
              textAlign = TextAlign.Center
            )

            Text(
              text = "${selectedRoom.roomCode} • ${selectedRoom.building}",
              fontSize = 12.sp,
              color = Color(0xFF475569),
              textAlign = TextAlign.Center,
              fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Capacity: ${selectedRoom.capacity} seats • Floor: ${selectedRoom.floor}",
              fontSize = 10.sp,
              color = Color(0xFF64748B),
              textAlign = TextAlign.Center
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Direct "Test Scan This Room" button
        Button(
          onClick = {
            onScanThisRoom(selectedRoom)
            onDismiss()
          },
          shape = RoundedCornerShape(100.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("scan_this_placard_button")
        ) {
          Icon(
            imageVector = Icons.Filled.FlashOn,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Scan ${selectedRoom.roomCode} & Report Issue ->",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color.White
          )
        }
      }
    }
  }
}

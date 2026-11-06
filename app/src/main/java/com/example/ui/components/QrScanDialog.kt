package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CampusRoom
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextNeonHighlight
import com.example.ui.theme.TextNeonPrimary
import com.example.ui.theme.TextNeonSecondary
import com.example.ui.theme.TextNeonTertiary
import com.example.ui.theme.UccGold

@Composable
fun QrScanDialog(
  availableRooms: List<CampusRoom>,
  onRoomScanned: (CampusRoom) -> Unit,
  onDismiss: () -> Unit
) {
  var manualCode by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  // Laser scan line animation
  val infiniteTransition = rememberInfiniteTransition(label = "laser_anim")
  val scanProgress by infiniteTransition.animateFloat(
    initialValue = 0.1f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_y"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.95f))
        .padding(20.dp)
        .testTag("qr_scanner_dialog")
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .background(Color.White.copy(alpha = 0.12f), CircleShape)
              .testTag("close_qr_scanner")
          ) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = "Close",
              tint = Color.White
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Scan UCC Room QR",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = UccGold.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "UCC",
                  color = UccGold,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "Point camera at door or podium sticker",
              color = TextNeonSecondary,
              fontSize = 11.sp
            )
          }

          IconButton(
            onClick = { /* Flash feedback */ },
            modifier = Modifier.background(Color.White.copy(alpha = 0.12f), CircleShape)
          ) {
            Icon(
              imageVector = Icons.Filled.FlashOn,
              contentDescription = "Flash",
              tint = UccGold
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Scanner Viewfinder Area
        Box(
          modifier = Modifier
            .size(260.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(DarkSurfaceElevated)
            .border(2.dp, NeonMagenta.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val bracketLen = 32.dp.toPx()
            val stroke = 4.dp.toPx()
            val pad = 16.dp.toPx()
            val bracketColor = NeonMagenta

            // Top-Left
            drawLine(bracketColor, Offset(pad, pad), Offset(pad + bracketLen, pad), stroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(pad, pad), Offset(pad, pad + bracketLen), stroke, StrokeCap.Round)

            // Top-Right
            drawLine(bracketColor, Offset(size.width - pad, pad), Offset(size.width - pad - bracketLen, pad), stroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(size.width - pad, pad), Offset(size.width - pad, pad + bracketLen), stroke, StrokeCap.Round)

            // Bottom-Left
            drawLine(bracketColor, Offset(pad, size.height - pad), Offset(pad + bracketLen, size.height - pad), stroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(pad, size.height - pad), Offset(pad, size.height - pad - bracketLen), stroke, StrokeCap.Round)

            // Bottom-Right
            drawLine(bracketColor, Offset(size.width - pad, size.height - pad), Offset(size.width - pad - bracketLen, size.height - pad), stroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(size.width - pad, size.height - pad), Offset(size.width - pad, size.height - pad - bracketLen), stroke, StrokeCap.Round)

            // Laser line sweeping
            val laserY = size.height * scanProgress
            drawLine(
              color = NeonMagenta,
              start = Offset(pad + 10f, laserY),
              end = Offset(size.width - pad - 10f, laserY),
              strokeWidth = 3.dp.toPx(),
              cap = StrokeCap.Round
            )
          }

          Icon(
            imageVector = Icons.Filled.QrCodeScanner,
            contentDescription = null,
            tint = NeonPurple.copy(alpha = 0.25f),
            modifier = Modifier.size(90.dp)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick-Simulate UCC Lecture Theatre QR
        Text(
          text = "Tap a UCC Lecture Theatre Sticker to test:",
          color = TextNeonHighlight,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(availableRooms) { room ->
            Surface(
              color = DarkSurfaceVariant,
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta.copy(alpha = 0.4f)),
              modifier = Modifier
                .clickable { onRoomScanned(room) }
                .testTag("scan_room_${room.roomCode}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.QrCode,
                  contentDescription = null,
                  tint = NeonMagenta,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                  Text(
                    text = room.roomCode,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = room.roomName,
                    color = TextNeonSecondary,
                    fontSize = 10.sp,
                    maxLines = 1
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Manual room code input fallback
        Text(
          text = "Or enter UCC Classroom/Theatre ID manually",
          color = TextNeonTertiary,
          fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = manualCode,
            onValueChange = {
              manualCode = it
              errorText = null
            },
            placeholder = { Text("e.g. Sandwich, NLT, CALC, LT, CODE", color = TextNeonTertiary, fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("manual_room_code_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = NeonMagenta,
              unfocusedBorderColor = DarkBorder,
              focusedContainerColor = DarkSurfaceElevated,
              unfocusedContainerColor = DarkSurfaceElevated,
              focusedTextColor = TextNeonPrimary,
              unfocusedTextColor = TextNeonPrimary
            )
          )

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = {
              val match = availableRooms.find {
                it.roomCode.equals(manualCode.trim(), ignoreCase = true) ||
                    it.roomName.contains(manualCode.trim(), ignoreCase = true)
              }
              if (match != null) {
                onRoomScanned(match)
              } else {
                errorText = "Theatre not found. Try 'SW-LH', 'NLT', 'CALC', 'LT' or 'CODE'"
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("manual_room_submit_button")
          ) {
            Text("Select", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }

        if (errorText != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = errorText!!,
            color = Color(0xFFF43F5E),
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

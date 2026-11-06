package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCodeScanner
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CampusRoom
import com.example.data.model.FaultCategory
import com.example.data.model.UrgencyLevel
import com.example.ui.components.Animated3DReportVisual
import com.example.ui.theme.SkillAlleyBg
import com.example.ui.theme.SkillAlleyBorder
import com.example.ui.theme.SkillAlleyButtonDark
import com.example.ui.theme.SkillAlleyCard
import com.example.ui.theme.SkillAlleyTextMuted
import com.example.ui.theme.SkillAlleyTextPrimary
import com.example.ui.theme.SkillAlleyTextSecondary
import com.example.ui.theme.UccGold
import com.example.ui.viewmodel.CampusFixViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportFaultScreen(
  viewModel: CampusFixViewModel,
  initialCategory: FaultCategory? = null,
  initialSampleImage: Int? = null,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val selectedRoom by viewModel.selectedRoomForReport.collectAsState()
  val allRooms by viewModel.allRooms.collectAsState()

  var category by remember { mutableStateOf(initialCategory ?: FaultCategory.PROJECTOR) }
  var urgency by remember { mutableStateOf(UrgencyLevel.HIGH) }
  var description by remember { mutableStateOf("") }
  var attachedImageRes by remember { mutableStateOf(initialSampleImage ?: category.sampleImageRes) }

  // Default to Sandwich Lecture Hall or first room
  val currentRoom: CampusRoom? = selectedRoom ?: allRooms.firstOrNull()

  // Dynamic quick-suggestion chips tailored for UCC classrooms
  val suggestionChips = remember(category, currentRoom?.roomCode) {
    val roomLabel = currentRoom?.roomCode ?: "lecture hall"
    when (category) {
      FaultCategory.PROJECTOR -> listOf(
        "Ceiling projector in $roomLabel has loose HDMI",
        "Projector showing 'NO SIGNAL' screen",
        "Dim lamp / Replace projector bulb",
        "Microphone sound humming in PA system"
      )
      FaultCategory.FURNITURE -> listOf(
        "Lecture chairs in Row D armrests broken",
        "Detached student writing desk",
        "Missing screws / wobbly bench",
        "Lecturer podium cracked"
      )
      FaultCategory.LIGHTING -> listOf(
        "Overhead fluorescent tube buzzing & dark",
        "Entire front row blackout in $roomLabel",
        "Power socket sparks when plugging laptop",
        "Ceiling tile loose above lighting fixture"
      )
      FaultCategory.PLUMBING -> listOf(
        "Washroom tap leaking continuously",
        "Sink drain pipe blocked / overflowing",
        "No running water in $roomLabel washrooms",
        "Water splashing onto tiled walkway"
      )
      FaultCategory.AIR_CONDITIONING -> listOf(
        "Ceiling fans in $roomLabel not spinning",
        "Split AC blowing warm air only",
        "Loud rattling fan blade noise",
        "AC water dripping onto student seats"
      )
      FaultCategory.OTHER -> listOf(
        "Door lock jammed / won't open",
        "Window louver glass broken",
        "Whiteboard marker tray detached",
        "Spill hazard on walkway steps"
      )
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(SkillAlleyBg)
      .testTag("report_fault_screen"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(SkillAlleyCard)
            .border(1.dp, SkillAlleyBorder, CircleShape)
            .testTag("report_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = SkillAlleyTextPrimary
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Report Fault",
              fontSize = 19.sp,
              fontWeight = FontWeight.Bold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = SkillAlleyButtonDark,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "UCC GHANA",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "Fast reporting in under 30s • Direct dispatch",
            fontSize = 11.sp,
            color = SkillAlleyTextSecondary
          )
        }
      }
    }

    // Locked Room Card (QR Verified)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("locked_room_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(SkillAlleyButtonDark),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.LocationOn,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = currentRoom?.roomCode ?: "Select Theatre",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = SkillAlleyTextPrimary
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(100.dp)
                  ) {
                    Text(
                      text = "QR Verified",
                      color = Color(0xFF047857),
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = currentRoom?.roomName ?: "UCC Lecture Hall",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  color = SkillAlleyTextSecondary
                )
                Text(
                  text = "${currentRoom?.building ?: "Campus"} • ${currentRoom?.floor ?: "Ground Floor"}",
                  fontSize = 11.sp,
                  color = SkillAlleyTextMuted
                )
              }
            }

            IconButton(
              onClick = { viewModel.openQrScanner() },
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SkillAlleyBg)
                .border(1.dp, SkillAlleyBorder, CircleShape)
                .testTag("rescan_qr_button")
            ) {
              Icon(
                imageVector = Icons.Filled.QrCodeScanner,
                contentDescription = "Scan another room",
                tint = SkillAlleyTextPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Quick room switcher pill row for Sandwich, NLT, CALC, LT, CODE
          Text(
            text = "Switch UCC Lecture Theatre:",
            fontSize = 10.sp,
            color = SkillAlleyTextSecondary,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(6.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(allRooms) { r ->
              val isPicked = r.roomCode == currentRoom?.roomCode
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(100.dp))
                  .clickable { viewModel.selectRoomManually(r) },
                shape = RoundedCornerShape(100.dp),
                color = if (isPicked) SkillAlleyButtonDark else SkillAlleyBg,
                border = if (isPicked) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
              ) {
                Text(
                  text = r.roomCode,
                  fontSize = 10.sp,
                  fontWeight = if (isPicked) FontWeight.Bold else FontWeight.Medium,
                  color = if (isPicked) Color.White else SkillAlleyTextPrimary,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }
          }
        }
      }
    }

    // Attached Image Preview Card (Visual proof for technicians)
    item {
      Text(
        text = "1. Problem Photo (Visual Proof)",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = SkillAlleyTextPrimary
      )
      Spacer(modifier = Modifier.height(6.dp))

      if (attachedImageRes != null) {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(170.dp)
          ) {
            Image(
              painter = painterResource(id = attachedImageRes!!),
              contentDescription = "Fault Photo",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )

            // Remove button
            IconButton(
              onClick = { attachedImageRes = null },
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(32.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
              Icon(Icons.Filled.Close, contentDescription = "Remove photo", tint = Color.White, modifier = Modifier.size(16.dp))
            }

            Surface(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp),
              color = SkillAlleyButtonDark,
              shape = RoundedCornerShape(100.dp)
            ) {
              Text(
                text = "Photo Attached • ${category.displayName}",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }
        }
      } else {
        // Quick visual picker row
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          item {
            VisualAttachmentOption(
              label = "Spoilt Projector",
              imageRes = R.drawable.fault_spoilt_projector_1793844723958,
              onClick = {
                attachedImageRes = R.drawable.fault_spoilt_projector_1793844723958
                category = FaultCategory.PROJECTOR
              }
            )
          }
          item {
            VisualAttachmentOption(
              label = "Broken Chair",
              imageRes = R.drawable.fault_broken_chair_1793844710427,
              onClick = {
                attachedImageRes = R.drawable.fault_broken_chair_1793844710427
                category = FaultCategory.FURNITURE
              }
            )
          }
          item {
            VisualAttachmentOption(
              label = "Flickering Light",
              imageRes = R.drawable.fault_broken_light_1793844737319,
              onClick = {
                attachedImageRes = R.drawable.fault_broken_light_1793844737319
                category = FaultCategory.LIGHTING
              }
            )
          }
          item {
            VisualAttachmentOption(
              label = "Washroom Tap",
              imageRes = R.drawable.fault_leaking_tap_1793844751100,
              onClick = {
                attachedImageRes = R.drawable.fault_leaking_tap_1793844751100
                category = FaultCategory.PLUMBING
              }
            )
          }
          item {
            VisualAttachmentOption(
              label = "Poor Ventilation",
              imageRes = R.drawable.fault_ventilation_ac_1793925507876,
              onClick = {
                attachedImageRes = R.drawable.fault_ventilation_ac_1793925507876
                category = FaultCategory.AIR_CONDITIONING
              }
            )
          }
        }
      }
    }

    // 2. Pick Category
    item {
      Text(
        text = "2. What is faulty?",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = SkillAlleyTextPrimary
      )

      Spacer(modifier = Modifier.height(8.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val rows = FaultCategory.entries.chunked(2)
        rows.forEach { pair ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            pair.forEach { cat ->
              val isSelected = cat == category
              Surface(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(16.dp))
                  .clickable {
                    category = cat
                    if (attachedImageRes == null) {
                      attachedImageRes = cat.sampleImageRes
                    }
                  }
                  .testTag("category_tile_${cat.name.lowercase()}"),
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) SkillAlleyButtonDark else SkillAlleyCard,
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(34.dp)
                      .clip(CircleShape)
                      .background(if (isSelected) Color(0xFF272A34) else SkillAlleyBg),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = cat.icon,
                      contentDescription = null,
                      tint = if (isSelected) Color.White else SkillAlleyTextPrimary,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = cat.displayName,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else SkillAlleyTextPrimary,
                    maxLines = 1
                  )
                }
              }
            }
          }
        }
      }
    }

    // Interactive 3D Model Inspection
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
        modifier = Modifier.fillMaxWidth().testTag("interactive_3d_inspection_card")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "3D Fault Inspector",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SkillAlleyTextPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = SkillAlleyButtonDark,
                shape = RoundedCornerShape(100.dp)
              ) {
                Text(
                  text = "Interactive 3D",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Tap 3D model to inspect perspective before dispatching UCC technicians.",
              fontSize = 11.sp,
              color = SkillAlleyTextSecondary,
              lineHeight = 15.sp
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Box(
            modifier = Modifier
              .size(90.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(SkillAlleyBg)
              .border(1.dp, SkillAlleyBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
          ) {
            Animated3DReportVisual(
              category = category,
              size = 85.dp,
              interactive = true
            )
          }
        }
      }
    }

    // 3. Urgency Selector
    item {
      Text(
        text = "3. Urgency Level",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = SkillAlleyTextPrimary
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        UrgencyLevel.entries.forEach { lvl ->
          val isSelected = lvl == urgency
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .clickable { urgency = lvl }
              .testTag("urgency_pill_${lvl.name.lowercase()}"),
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) SkillAlleyButtonDark else SkillAlleyCard,
            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = lvl.displayName.split(" ").first(),
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else SkillAlleyTextPrimary
              )
              Text(
                text = when (lvl) {
                  UrgencyLevel.HIGH -> "Lecture Starting!"
                  UrgencyLevel.MEDIUM -> "Affects class"
                  UrgencyLevel.LOW -> "Routine"
                },
                fontSize = 9.sp,
                color = if (isSelected) Color(0xFFCBD5E1) else SkillAlleyTextSecondary
              )
            }
          }
        }
      }
    }

    // 4. Quick Suggestion Chips & Notes
    item {
      Text(
        text = "4. Quick Description",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = SkillAlleyTextPrimary
      )

      Spacer(modifier = Modifier.height(6.dp))

      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        suggestionChips.forEach { chipText ->
          val isPicked = description == chipText
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(100.dp))
              .clickable {
                description = if (isPicked) "" else chipText
              },
            shape = RoundedCornerShape(100.dp),
            color = if (isPicked) SkillAlleyButtonDark else SkillAlleyCard,
            border = if (isPicked) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (isPicked) {
                Icon(
                  imageVector = Icons.Filled.Check,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
              }
              Text(
                text = chipText,
                fontSize = 11.sp,
                color = if (isPicked) Color.White else SkillAlleyTextPrimary,
                fontWeight = if (isPicked) FontWeight.SemiBold else FontWeight.Normal
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        placeholder = { Text("Add specific notes for UCC technicians...", fontSize = 12.sp, color = SkillAlleyTextMuted) },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("fault_description_input"),
        shape = RoundedCornerShape(16.dp),
        minLines = 3,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = SkillAlleyButtonDark,
          unfocusedBorderColor = SkillAlleyBorder,
          focusedContainerColor = SkillAlleyCard,
          unfocusedContainerColor = SkillAlleyCard,
          focusedTextColor = SkillAlleyTextPrimary,
          unfocusedTextColor = SkillAlleyTextPrimary
        )
      )
    }

    // Submit Button
    item {
      Spacer(modifier = Modifier.height(8.dp))

      Button(
        onClick = {
          if (currentRoom != null) {
            viewModel.submitFaultReport(
              room = currentRoom,
              category = category,
              urgency = urgency,
              description = description,
              sampleImageRes = attachedImageRes
            )
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("submit_report_button"),
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.FlashOn,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Dispatch UCC Maintenance ->",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

@Composable
private fun VisualAttachmentOption(
  label: String,
  imageRes: Int,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .width(115.dp)
      .clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
  ) {
    Column {
      Image(
        painter = painterResource(id = imageRes),
        contentDescription = label,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .fillMaxWidth()
          .height(65.dp)
          .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      )
      Text(
        text = label,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = SkillAlleyTextPrimary,
        modifier = Modifier.padding(6.dp),
        maxLines = 1
      )
    }
  }
}

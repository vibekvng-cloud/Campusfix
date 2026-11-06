package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReportStatus
import com.example.ui.components.FaultReportCard
import com.example.ui.theme.SkillAlleyBg
import com.example.ui.theme.SkillAlleyBorder
import com.example.ui.theme.SkillAlleyButtonDark
import com.example.ui.theme.SkillAlleyCard
import com.example.ui.theme.SkillAlleyTextMuted
import com.example.ui.theme.SkillAlleyTextPrimary
import com.example.ui.theme.SkillAlleyTextSecondary
import com.example.ui.viewmodel.CampusFixViewModel

@Composable
fun MyReportsScreen(
  viewModel: CampusFixViewModel,
  onNewReportClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val reports by viewModel.allReports.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var selectedStatusFilter by remember { mutableStateOf<ReportStatus?>(null) }

  val filteredReports = reports.filter { report ->
    val matchesStatus = selectedStatusFilter == null || report.status == selectedStatusFilter
    val matchesSearch = searchQuery.isBlank() ||
        report.roomCode.contains(searchQuery, ignoreCase = true) ||
        report.roomName.contains(searchQuery, ignoreCase = true) ||
        report.category.displayName.contains(searchQuery, ignoreCase = true) ||
        report.description.contains(searchQuery, ignoreCase = true)
    matchesStatus && matchesSearch
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(SkillAlleyBg)
      .testTag("my_reports_screen"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "UCC Fault Reports",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = SkillAlleyButtonDark,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "CAPE COAST",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "${reports.size} total reports logged across lecture halls",
            fontSize = 12.sp,
            color = SkillAlleyTextSecondary
          )
        }

        Button(
          onClick = onNewReportClick,
          shape = RoundedCornerShape(100.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
          modifier = Modifier.testTag("new_report_fab")
        ) {
          Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Report ->", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }

    // Search bar
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search by room (e.g. Sandwich, NLT, CALC)...", fontSize = 12.sp, color = SkillAlleyTextMuted) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = SkillAlleyTextSecondary,
            modifier = Modifier.size(18.dp)
          )
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_reports_input"),
        shape = RoundedCornerShape(100.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = SkillAlleyCard,
          unfocusedContainerColor = SkillAlleyCard,
          focusedBorderColor = SkillAlleyButtonDark,
          unfocusedBorderColor = SkillAlleyBorder,
          focusedTextColor = SkillAlleyTextPrimary,
          unfocusedTextColor = SkillAlleyTextPrimary
        )
      )
    }

    // Filter Chips
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        item {
          val isAll = selectedStatusFilter == null
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(100.dp))
              .clickable { selectedStatusFilter = null }
              .testTag("filter_all"),
            shape = RoundedCornerShape(100.dp),
            color = if (isAll) SkillAlleyButtonDark else SkillAlleyCard,
            border = if (isAll) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
          ) {
            Text(
              text = "All (${reports.size})",
              fontSize = 11.sp,
              fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium,
              color = if (isAll) Color.White else SkillAlleyTextPrimary,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
            )
          }
        }

        items(ReportStatus.entries) { status ->
          val isSelected = selectedStatusFilter == status
          val count = reports.count { it.status == status }
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(100.dp))
              .clickable { selectedStatusFilter = status }
              .testTag("filter_${status.name.lowercase()}"),
            shape = RoundedCornerShape(100.dp),
            color = if (isSelected) SkillAlleyButtonDark else SkillAlleyCard,
            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .background(status.textColor, RoundedCornerShape(100.dp))
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${status.displayName} ($count)",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else SkillAlleyTextPrimary
              )
            }
          }
        }
      }
    }

    // Reports count & clear filter row
    if (selectedStatusFilter != null || searchQuery.isNotBlank()) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Showing ${filteredReports.size} results",
            fontSize = 12.sp,
            color = SkillAlleyTextSecondary,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "Reset Filter",
            fontSize = 12.sp,
            color = SkillAlleyButtonDark,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
              .clickable {
                selectedStatusFilter = null
                searchQuery = ""
              }
              .testTag("reset_filter_btn")
          )
        }
      }
    }

    // Empty state
    if (filteredReports.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
        ) {
          Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "No fault reports match",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = SkillAlleyTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Try clearing your search query or status filter",
              fontSize = 12.sp,
              color = SkillAlleyTextSecondary
            )
          }
        }
      }
    } else {
      items(filteredReports, key = { it.id }) { report ->
        FaultReportCard(
          report = report,
          onClick = { viewModel.selectReportDetail(report) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

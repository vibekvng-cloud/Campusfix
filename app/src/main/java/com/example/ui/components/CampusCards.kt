package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FaultCategory
import com.example.data.model.FaultReport
import com.example.data.model.ReportStatus
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderGlow
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGradientEnd
import com.example.ui.theme.NeonGradientMiddle
import com.example.ui.theme.NeonGradientStart
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextNeonHighlight
import com.example.ui.theme.TextNeonPrimary
import com.example.ui.theme.TextNeonSecondary
import com.example.ui.theme.TextNeonTertiary
import com.example.ui.theme.UccGold
import com.example.ui.theme.SkillAlleyBadgeCircle
import com.example.ui.theme.SkillAlleyBg
import com.example.ui.theme.SkillAlleyBorder
import com.example.ui.theme.SkillAlleyBorderSubtle
import com.example.ui.theme.SkillAlleyButtonDark
import com.example.ui.theme.SkillAlleyCard
import com.example.ui.theme.SkillAlleyDarkCard
import com.example.ui.theme.SkillAlleyDarkSurface
import com.example.ui.theme.SkillAlleyPillActive
import com.example.ui.theme.SkillAlleyPillInactive
import com.example.ui.theme.SkillAlleyTextMuted
import com.example.ui.theme.SkillAlleyTextPrimary
import com.example.ui.theme.SkillAlleyTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HeroNeonCard(
  totalActive: Int,
  totalResolved: Int,
  avgFixTimeMin: Int,
  userRoleName: String,
  onScanClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("hero_neon_card"),
    shape = RoundedCornerShape(26.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, DarkBorderGlow)
  ) {
    Box(
      modifier = Modifier
        .background(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0x35E81CFF),
              Color(0x15A855F7),
              Color(0x00000000)
            ),
            center = Offset(800f, 100f),
            radius = 900f
          )
        )
        .padding(20.dp)
    ) {
      Column {
        // UCC Header Tag
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = UccGold.copy(alpha = 0.18f),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, UccGold.copy(alpha = 0.4f))
            ) {
              Text(
                text = "UCC GHANA",
                color = UccGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                letterSpacing = 0.8.sp
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Campus Maintenance",
              color = TextNeonSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }

          // User role badge
          Surface(
            color = NeonMagenta.copy(alpha = 0.15f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta.copy(alpha = 0.5f))
          ) {
            Text(
              text = userRoleName,
              color = TextNeonHighlight,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Large Main Stat
        Text(
          text = "$totalActive Active Campus Jobs",
          color = TextNeonPrimary,
          fontSize = 25.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Sandwich • NLT • CALC • LT • CODE",
          color = NeonPurple,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Metrics pills
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          MetricPill(label = "In Progress", value = "$totalActive jobs", accentColor = NeonMagenta)
          MetricPill(label = "Fixed Today", value = "$totalResolved", accentColor = Color(0xFF10B981))
          MetricPill(label = "UCC Avg Fix", value = "${avgFixTimeMin}m", accentColor = NeonCyan)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Glowing Scan QR Action Bar
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onScanClick() }
            .testTag("hero_scan_qr_button"),
          color = Color(0x33A855F7),
          shape = RoundedCornerShape(18.dp),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonMagenta.copy(alpha = 0.7f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(
                    brush = Brush.linearGradient(
                      listOf(NeonGradientStart, NeonGradientEnd)
                    )
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.QrCodeScanner,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Scan Lecture Theatre QR Code",
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "One-handed • Locks location in under 30s",
                  color = TextNeonSecondary,
                  fontSize = 11.sp
                )
              }
            }

            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = NeonMagenta,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MetricPill(label: String, value: String, accentColor: Color) {
  Surface(
    color = DarkSurface,
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
  ) {
    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
      Text(
        text = label,
        color = TextNeonTertiary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        color = accentColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

/**
 * Visual problem card for beginner students to tap directly with authentic photos
 * (broken chairs, spoilt projectors, broken lights, leaking taps)
 */
@Composable
fun BeginnerVisualProblemCard(
  title: String,
  subtitle: String,
  imageRes: Int?,
  accentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .width(190.dp)
      .clickable { onClick() }
      .testTag("visual_card_${title.lowercase().replace(" ", "_")}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
  ) {
    Column {
      if (imageRes != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
        ) {
          Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
          // Subtle soft vignette
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(Color.Transparent, Color(0x22000000))
                )
              )
          )
        }
      }

      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = title,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = SkillAlleyTextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = SkillAlleyTextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          color = SkillAlleyButtonDark,
          shape = RoundedCornerShape(100.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "Tap to Report",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(10.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun QuickActionNeonPill(
  icon: ImageVector,
  label: String,
  accentColor: Color = NeonMagenta,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  badgeText: String? = null
) {
  Column(
    modifier = modifier
      .clickable { onClick() }
      .testTag("action_${label.lowercase().replace(" ", "_")}"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(54.dp)
        .clip(RoundedCornerShape(18.dp))
        .background(SkillAlleyCard)
        .border(1.2.dp, SkillAlleyBorder, RoundedCornerShape(18.dp)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = SkillAlleyTextPrimary,
        modifier = Modifier.size(24.dp)
      )
      if (badgeText != null) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(4.dp)
            .background(Color(0xFFEF4444), CircleShape)
            .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
          Text(
            text = badgeText,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      color = SkillAlleyTextPrimary
    )
  }
}

@Composable
fun FaultOverviewDonutCard(
  categoryCounts: Map<FaultCategory, Int>,
  totalCount: Int,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "UCC Faults Overview",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )
          Text(
            text = "Active breakdown across campus",
            fontSize = 11.sp,
            color = SkillAlleyTextSecondary
          )
        }
        Surface(
          color = SkillAlleyBg,
          shape = RoundedCornerShape(100.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
        ) {
          Text(
            text = "This Semester",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = SkillAlleyTextPrimary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Donut Chart Canvas
        Box(
          modifier = Modifier.size(130.dp),
          contentAlignment = Alignment.Center
        ) {
          NeonDonutCanvas(
            categoryCounts = categoryCounts,
            totalCount = totalCount
          )
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "$totalCount",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = TextNeonPrimary
            )
            Text(
              text = "Total UCC Jobs",
              fontSize = 9.sp,
              color = TextNeonSecondary
            )
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Legend with percentages
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categoryCounts.entries
            .sortedByDescending { it.value }
            .take(4)
            .forEach { entry ->
              val percentage = if (totalCount > 0) (entry.value * 100 / totalCount) else 0
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(entry.key.accentColor)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = entry.key.displayName,
                    fontSize = 11.sp,
                    color = TextNeonPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
                Text(
                  text = "$percentage%",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = entry.key.accentColor
                )
              }
            }
        }
      }
    }
  }
}

@Composable
private fun NeonDonutCanvas(
  categoryCounts: Map<FaultCategory, Int>,
  totalCount: Int
) {
  var animationPlayed by remember { mutableStateOf(false) }
  val curPercent by animateFloatAsState(
    targetValue = if (animationPlayed) 1f else 0f,
    animationSpec = tween(durationMillis = 900),
    label = "neon_donut_anim"
  )

  LaunchedEffect(key1 = true) {
    animationPlayed = true
  }

  Canvas(modifier = Modifier.fillMaxSize()) {
    val strokeWidth = 16.dp.toPx()
    val diameter = size.minDimension - strokeWidth
    val arcSize = Size(diameter, diameter)
    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

    if (totalCount == 0) {
      drawArc(
        color = DarkBorder,
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth)
      )
      return@Canvas
    }

    var startAngle = -90f
    categoryCounts.forEach { (category, count) ->
      if (count > 0) {
        val sweepAngle = (count.toFloat() / totalCount.toFloat()) * 360f * curPercent
        drawArc(
          color = category.accentColor,
          startAngle = startAngle,
          sweepAngle = sweepAngle - 3f,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        startAngle += sweepAngle
      }
    }
  }
}

@Composable
fun FaultReportCard(
  report: FaultReport,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val timeFormat = SimpleDateFormat("h:mm a • MMM d", Locale.getDefault())
  val formattedDate = timeFormat.format(Date(report.createdAtMillis))

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("report_card_${report.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Thumbnail image if available, otherwise category icon
      if (report.sampleImageRes != null) {
        Box(
          modifier = Modifier
            .size(62.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, SkillAlleyBorder, RoundedCornerShape(14.dp))
        ) {
          Image(
            painter = painterResource(id = report.sampleImageRes),
            contentDescription = report.category.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }
      } else {
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SkillAlleyBg)
            .border(1.dp, SkillAlleyBorder, RoundedCornerShape(16.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = report.category.icon,
            contentDescription = report.category.displayName,
            tint = SkillAlleyTextPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      // Content
      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = report.category.displayName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SkillAlleyTextPrimary
          )

          // Status Badge
          Surface(
            color = report.status.bgColor,
            shape = RoundedCornerShape(100.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, report.status.textColor.copy(alpha = 0.3f))
          ) {
            Text(
              text = report.status.displayName,
              color = report.status.textColor,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = SkillAlleyTextPrimary,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${report.roomName} (${report.roomCode})",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = SkillAlleyTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = report.description,
          fontSize = 11.sp,
          color = SkillAlleyTextMuted,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Priority chip
          Surface(
            color = report.urgency.bgColor,
            shape = RoundedCornerShape(100.dp)
          ) {
            Text(
              text = report.urgency.displayName,
              color = report.urgency.textColor,
              fontSize = 9.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
            )
          }

          Text(
            text = formattedDate,
            fontSize = 10.sp,
            color = SkillAlleyTextMuted
          )
        }
      }
    }
  }
}

@Composable
fun StatusProgressStepper(
  currentStatus: ReportStatus,
  modifier: Modifier = Modifier
) {
  val steps = ReportStatus.entries
  val activeIndex = currentStatus.stepIndex

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      steps.forEachIndexed { index, step ->
        val isReached = index <= activeIndex
        val isCurrent = index == activeIndex

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(
                if (isReached) NeonMagenta else DarkSurfaceVariant
              )
              .border(
                1.dp,
                if (isReached) NeonMagenta else DarkBorder,
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            if (index < activeIndex) {
              Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            } else {
              Text(
                text = "${index + 1}",
                color = if (isReached) Color.White else TextNeonTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = step.displayName,
            fontSize = 10.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
            color = if (isReached) TextNeonPrimary else TextNeonTertiary
          )
        }
      }
    }
  }
}

/**
 * Skillalley Clean Style 3D Hero Card:
 * - Crisp white rounded card
 * - Left side: "Smart 3D Campus Diagnostics", description, black pill button "Report Fault ->"
 * - Right side: Interactive Animated3DReportVisual with fault switcher pills (Chairs, Projector, AC, Tap, Lights)
 * - Badges: 85 (Resolution rate), 38m (Response time)
 */
@Composable
fun SkillAlley3DHeroCard(
  totalActive: Int,
  totalResolved: Int,
  onReportClick: (FaultCategory) -> Unit,
  onScanClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var activeCategory by remember { mutableStateOf(FaultCategory.PROJECTOR) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("skillalley_3d_hero_card"),
    shape = RoundedCornerShape(26.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      // Top Row with UCC Badge & Health Score (matching Skillalley badge circle 75/85)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = SkillAlleyBg,
            shape = RoundedCornerShape(100.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF10B981))
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "UCC Works Active Dispatch",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SkillAlleyTextPrimary
              )
            }
          }
        }

        // Circular score badge matching 75 / 85 in Skillalley image
        Surface(
          shape = CircleShape,
          color = SkillAlleyBg,
          border = androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder),
          modifier = Modifier.size(34.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "85",
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold,
              color = SkillAlleyTextPrimary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Main Hero Row: Title & Action on Left, 3D Interactive Model on Right
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Smart 3D Campus Fault Diagnostics.",
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SkillAlleyTextPrimary,
            letterSpacing = (-0.5).sp,
            lineHeight = 24.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Inspect interactive 3D models of classroom faults before reporting.",
            fontSize = 12.sp,
            color = SkillAlleyTextSecondary,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Dark Pill Button with Arrow -> matching "Upgrade Now ->" in uploaded image
          Button(
            onClick = { onReportClick(activeCategory) },
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkillAlleyButtonDark),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
            modifier = Modifier.testTag("hero_report_now_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Report Fault",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // 3D Animated Illustration on Right
        Box(
          modifier = Modifier
            .size(125.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SkillAlleyBg)
            .border(1.dp, SkillAlleyBorderSubtle, RoundedCornerShape(20.dp)),
          contentAlignment = Alignment.Center
        ) {
          Animated3DReportVisual(
            category = activeCategory,
            size = 115.dp,
            interactive = true
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3D Fault Type Selector Pills (Chairs, Projector, AC, Tap, Lights)
      Text(
        text = "Select 3D Inspection Model:",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = SkillAlleyTextMuted
      )
      Spacer(modifier = Modifier.height(6.dp))
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        val categories = listOf(
          FaultCategory.PROJECTOR to "Projector",
          FaultCategory.FURNITURE to "Broken Chair",
          FaultCategory.AIR_CONDITIONING to "Ventilation/AC",
          FaultCategory.PLUMBING to "Leaking Tap",
          FaultCategory.LIGHTING to "Broken Lights"
        )
        items(categories) { (cat, label) ->
          val isSelected = activeCategory == cat
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(100.dp))
              .clickable { activeCategory = cat }
              .testTag("switch_3d_cat_${cat.name.lowercase()}"),
            shape = RoundedCornerShape(100.dp),
            color = if (isSelected) SkillAlleyButtonDark else SkillAlleyBg,
            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, SkillAlleyBorder)
          ) {
            Text(
              text = label,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else SkillAlleyTextSecondary,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * Skillalley Inset Dark Floating Process Card:
 * Modeled directly after "The Design Engineering Process" dark card in the screenshot.
 */
@Composable
fun SkillAlleyDarkProcessCard(
  onViewQueue: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("skillalley_dark_process_card"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = SkillAlleyDarkCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Row(
      modifier = Modifier.padding(18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // 3D Isometric icon on the left
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(SkillAlleyDarkSurface),
        contentAlignment = Alignment.Center
      ) {
        Animated3DReportVisual(
          category = FaultCategory.FURNITURE,
          size = 68.dp,
          interactive = false
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "11:00 - 12:30",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            color = Color(0xFF334155),
            shape = CircleShape,
            modifier = Modifier.size(22.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text("85", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "The Rapid Maintenance Process",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )

        Text(
          text = "QR Scan -> Works Dept Dispatch -> Repair in < 38 mins.",
          fontSize = 11.sp,
          color = Color(0xFF94A3B8),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      IconButton(
        onClick = onViewQueue,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(Color(0xFF272A34))
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "View Queue",
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

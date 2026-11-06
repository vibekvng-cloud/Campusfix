package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.FaultCategory
import kotlin.math.cos
import kotlin.math.sin

/**
 * Interactive, lightweight 3D Isometric Animated Visual for Campus Fault Reports.
 * Provides subtle 3D perspective, hovering float motion, and category-specific
 * 3D elements (Broken Chair, Spoilt Projector, Poor Ventilation AC, Leaking Tap, Broken Light).
 */
@Composable
fun Animated3DReportVisual(
  category: FaultCategory,
  modifier: Modifier = Modifier,
  size: Dp = 120.dp,
  interactive: Boolean = true
) {
  var isTapped by remember { mutableStateOf(false) }

  // Interactive tap spring reaction
  val tapScale by animateFloatAsState(
    targetValue = if (isTapped) 1.08f else 1.0f,
    animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
    finishedListener = { isTapped = false }
  )

  // Infinite subtle floating hover oscillation
  val infiniteTransition = rememberInfiniteTransition(label = "3d_float")

  val hoverY by infiniteTransition.animateFloat(
    initialValue = -5f,
    targetValue = 5f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "hoverY"
  )

  val rotationAngle by infiniteTransition.animateFloat(
    initialValue = -4f,
    targetValue = 4f,
    animationSpec = infiniteRepeatable(
      animation = tween(3000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "rotAngle"
  )

  val pulseAnim by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  val flowProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1800, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "flow"
  )

  Box(
    modifier = modifier
      .size(size)
      .graphicsLayer {
        // Subtle 3D perspective transformation
        cameraDistance = 16f * density
        rotationY = rotationAngle
        rotationX = -hoverY * 0.6f
        translationY = hoverY
        scaleX = tapScale
        scaleY = tapScale
      }
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = interactive
      ) {
        isTapped = true
      },
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = this.size.width
      val h = this.size.height
      val cx = w / 2f
      val cy = h / 2f

      // 1. Base 3D Isometric Floating Platform
      drawIsometricBasePlatform(cx, cy + h * 0.22f, w * 0.42f, h * 0.18f, category.accentColor)

      // 2. Specific 3D Illustrated Subject
      when (category) {
        FaultCategory.FURNITURE -> {
          draw3DBrokenChair(cx, cy, w, h, pulseAnim)
        }
        FaultCategory.PROJECTOR -> {
          draw3DSpoiltProjector(cx, cy, w, h, pulseAnim)
        }
        FaultCategory.AIR_CONDITIONING -> {
          draw3DPoorVentilation(cx, cy, w, h, flowProgress, pulseAnim)
        }
        FaultCategory.PLUMBING -> {
          draw3DLeakingTap(cx, cy, w, h, flowProgress)
        }
        FaultCategory.LIGHTING -> {
          draw3DBrokenLight(cx, cy, w, h, pulseAnim)
        }
        FaultCategory.OTHER -> {
          draw3DGeneralWarning(cx, cy, w, h, pulseAnim)
        }
      }
    }
  }
}

/**
 * 3D Isometric Base Pedestal with subtle gradient shadow
 */
private fun DrawScope.drawIsometricBasePlatform(
  cx: Float,
  cy: Float,
  rx: Float,
  ry: Float,
  accentColor: Color
) {
  // Soft ground shadow
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent),
      center = Offset(cx, cy + ry * 0.8f),
      radius = rx * 1.2f
    ),
    topLeft = Offset(cx - rx * 1.1f, cy),
    size = Size(rx * 2.2f, ry * 1.4f)
  )

  // Isometric Diamond Base Plate
  val topPath = Path().apply {
    moveTo(cx, cy - ry)
    lineTo(cx + rx, cy)
    lineTo(cx, cy + ry)
    lineTo(cx - rx, cy)
    close()
  }

  // Plate top face
  drawPath(
    path = topPath,
    brush = Brush.linearGradient(
      colors = listOf(Color(0xFF262A36), Color(0xFF161820)),
      start = Offset(cx - rx, cy - ry),
      end = Offset(cx + rx, cy + ry)
    )
  )

  // Outline glow
  drawPath(
    path = topPath,
    color = accentColor.copy(alpha = 0.45f),
    style = Stroke(width = 2.dp.toPx())
  )

  // Plate front thickness
  val thickness = 10.dp.toPx()
  val frontPath = Path().apply {
    moveTo(cx - rx, cy)
    lineTo(cx, cy + ry)
    lineTo(cx, cy + ry + thickness)
    lineTo(cx - rx, cy + thickness)
    close()
  }
  drawPath(frontPath, color = Color(0xFF101217))

  val rightPath = Path().apply {
    moveTo(cx, cy + ry)
    lineTo(cx + rx, cy)
    lineTo(cx + rx, cy + thickness)
    lineTo(cx, cy + ry + thickness)
    close()
  }
  drawPath(rightPath, color = Color(0xFF0C0E12))
}

/**
 * 3D Broken Chair & Desk: Isometric Lecture Hall Chair with cracked weld joint and stress pulse
 */
private fun DrawScope.draw3DBrokenChair(
  cx: Float,
  cy: Float,
  w: Float,
  h: Float,
  pulse: Float
) {
  val unit = w / 10f

  // Chair Backrest (Isometric wooden slab)
  val backrest = Path().apply {
    moveTo(cx - unit * 1.2f, cy - unit * 2.4f)
    lineTo(cx + unit * 1.2f, cy - unit * 1.8f)
    lineTo(cx + unit * 1.2f, cy - unit * 0.6f)
    lineTo(cx - unit * 1.2f, cy - unit * 1.2f)
    close()
  }
  drawPath(
    backrest,
    brush = Brush.linearGradient(
      listOf(Color(0xFFB45309), Color(0xFF78350F))
    )
  )

  // Chair Seat Base (Isometric horizontal wooden plate)
  val seat = Path().apply {
    moveTo(cx - unit * 1.4f, cy - unit * 0.9f)
    lineTo(cx + unit * 1.4f, cy - unit * 0.3f)
    lineTo(cx + unit * 0.6f, cy + unit * 0.6f)
    lineTo(cx - unit * 2.0f, cy)
    close()
  }
  drawPath(
    seat,
    brush = Brush.linearGradient(
      listOf(Color(0xFFD97706), Color(0xFF92400E))
    )
  )

  // Steel Legs
  val legStroke = 3.5.dp.toPx()
  val legColor = Color(0xFF94A3B8)

  // Back leg
  drawLine(legColor, Offset(cx + unit * 0.8f, cy - unit * 0.3f), Offset(cx + unit * 0.8f, cy + unit * 1.6f), strokeWidth = legStroke, cap = StrokeCap.Round)
  // Left leg
  drawLine(legColor, Offset(cx - unit * 1.6f, cy), Offset(cx - unit * 1.6f, cy + unit * 1.9f), strokeWidth = legStroke, cap = StrokeCap.Round)
  // Right front leg (Broken / Disconnected Leg with crack)
  val brokenPoint = Offset(cx + unit * 0.4f, cy + unit * 0.8f)
  drawLine(legColor, Offset(cx + unit * 0.4f, cy + unit * 0.4f), brokenPoint, strokeWidth = legStroke, cap = StrokeCap.Round)

  // Cracked broken lower piece tilted
  drawLine(
    Color(0xFFEF4444),
    brokenPoint,
    Offset(cx + unit * 0.9f, cy + unit * 1.7f),
    strokeWidth = legStroke,
    cap = StrokeCap.Round
  )

  // Dynamic Animated Glowing Fracture Ring
  drawCircle(
    color = Color(0xFFEF4444).copy(alpha = pulse),
    radius = (6f + pulse * 6f).dp.toPx(),
    center = brokenPoint,
    style = Stroke(width = 2.dp.toPx())
  )
  drawCircle(
    color = Color(0xFFEF4444),
    radius = 3.5.dp.toPx(),
    center = brokenPoint
  )

  // Floating Warning Exclamation badge above backrest
  val alertPos = Offset(cx + unit * 1.4f, cy - unit * 2.8f)
  drawCircle(
    color = Color(0xFFF59E0B),
    radius = 7.dp.toPx(),
    center = alertPos
  )
  drawLine(
    color = Color.Black,
    start = Offset(alertPos.x, alertPos.y - 3.dp.toPx()),
    end = Offset(alertPos.x, alertPos.y + 1.dp.toPx()),
    strokeWidth = 2.dp.toPx(),
    cap = StrokeCap.Round
  )
  drawCircle(Color.Black, 1.dp.toPx(), Offset(alertPos.x, alertPos.y + 3.5.dp.toPx()))
}

/**
 * 3D Spoilt Projector: Overhead ceiling box with projection cone & blinking error beam
 */
private fun DrawScope.draw3DSpoiltProjector(
  cx: Float,
  cy: Float,
  w: Float,
  h: Float,
  pulse: Float
) {
  val unit = w / 10f

  // Ceiling mounting bracket
  val bracket = Offset(cx, cy - unit * 2.8f)
  drawLine(Color(0xFF64748B), bracket, Offset(cx, cy - unit * 1.8f), strokeWidth = 3.dp.toPx())

  // Projector 3D Box Body
  val pWidth = unit * 3.6f
  val pHeight = unit * 1.4f
  val pDepth = unit * 2.0f

  // Front Face
  val front = Path().apply {
    moveTo(cx - pWidth / 2, cy - unit * 1.8f)
    lineTo(cx + pWidth / 2, cy - unit * 1.8f)
    lineTo(cx + pWidth / 2, cy - unit * 1.8f + pHeight)
    lineTo(cx - pWidth / 2, cy - unit * 1.8f + pHeight)
    close()
  }
  drawPath(front, Color(0xFFF8FAFC))

  // Lens Rim
  val lensCenter = Offset(cx - unit * 0.6f, cy - unit * 1.8f + pHeight * 0.5f)
  drawCircle(Color(0xFF1E293B), radius = unit * 0.55f, center = lensCenter)
  drawCircle(Color(0xFF38BDF8), radius = unit * 0.35f, center = lensCenter)

  // Flashing Warning LED
  val ledPos = Offset(cx + unit * 1.0f, cy - unit * 1.8f + pHeight * 0.3f)
  drawCircle(
    color = Color(0xFFEF4444).copy(alpha = pulse),
    radius = 4.dp.toPx(),
    center = ledPos
  )

  // Animated Flickering Projection Beam with "No Signal" Cone
  val beamPath = Path().apply {
    moveTo(lensCenter.x, lensCenter.y)
    lineTo(cx - unit * 2.6f, cy + unit * 1.8f)
    lineTo(cx + unit * 0.2f, cy + unit * 2.2f)
    close()
  }
  drawPath(
    path = beamPath,
    brush = Brush.verticalGradient(
      colors = listOf(
        Color(0xFFE81CFF).copy(alpha = 0.5f * pulse),
        Color(0xFF6366F1).copy(alpha = 0.15f * pulse),
        Color.Transparent
      ),
      startY = lensCenter.y,
      endY = cy + unit * 2.2f
    )
  )

  // Beam Scanlines
  drawLine(
    color = Color(0xFFF43F5E).copy(alpha = pulse * 0.8f),
    start = Offset(cx - unit * 1.8f, cy + unit * 0.6f),
    end = Offset(cx - unit * 0.2f, cy + unit * 0.8f),
    strokeWidth = 1.5.dp.toPx()
  )
}

/**
 * 3D Poor Ventilation / Broken AC: AC cassette unit with louvers, dust filter & animated airflow streams
 */
private fun DrawScope.draw3DPoorVentilation(
  cx: Float,
  cy: Float,
  w: Float,
  h: Float,
  flow: Float,
  pulse: Float
) {
  val unit = w / 10f

  // AC Wall Chassis (Sleek Isometric Block)
  val acLeft = cx - unit * 3.2f
  val acTop = cy - unit * 2.2f
  val acWidth = unit * 6.4f
  val acHeight = unit * 2.4f

  // Main chassis body
  drawRoundRect(
    brush = Brush.linearGradient(
      listOf(Color(0xFFF1F5F9), Color(0xFFCBD5E1))
    ),
    topLeft = Offset(acLeft, acTop),
    size = Size(acWidth, acHeight),
    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
  )

  // Front airflow louvers (grille slats)
  val louverColor = Color(0xFF475569)
  for (i in 1..4) {
    val yPos = acTop + acHeight * (0.35f + i * 0.12f)
    drawLine(
      louverColor,
      Offset(acLeft + unit * 0.6f, yPos),
      Offset(acLeft + acWidth - unit * 0.6f, yPos),
      strokeWidth = 2.dp.toPx()
    )
  }

  // Drooping / Broken Filter Panel on the right (like the real UCC classroom AC)
  val droopPath = Path().apply {
    moveTo(acLeft + acWidth - unit * 1.6f, acTop + acHeight * 0.7f)
    lineTo(acLeft + acWidth - unit * 0.2f, acTop + acHeight * 1.3f)
    lineTo(acLeft + acWidth - unit * 0.4f, acTop + acHeight * 1.4f)
    lineTo(acLeft + acWidth - unit * 1.8f, acTop + acHeight * 0.8f)
    close()
  }
  drawPath(droopPath, Color(0xFF94A3B8))

  // Airflow / Ventilation Wind Streamlines (Flowing outward with animated dash offset)
  for (line in 0..2) {
    val startX = acLeft + unit * 1.2f + line * unit * 1.8f
    val startY = acTop + acHeight + 2.dp.toPx()

    val wavePhase = (flow * 360f + line * 60f) * (Math.PI / 180f).toFloat()
    val endX = startX + sin(wavePhase) * unit * 0.6f
    val endY = startY + unit * 1.8f

    drawLine(
      brush = Brush.verticalGradient(
        listOf(
          Color(0xFFA855F7).copy(alpha = 0.8f),
          Color(0xFF06B6D4).copy(alpha = 0.2f)
        ),
        startY = startY,
        endY = endY
      ),
      start = Offset(startX, startY),
      end = Offset(endX, endY),
      strokeWidth = 2.5.dp.toPx(),
      cap = StrokeCap.Round
    )

    // Floating Air particles / dust
    val pY = startY + (flow * unit * 1.8f + line * unit * 0.4f) % (unit * 1.8f)
    drawCircle(
      color = Color(0xFFA855F7).copy(alpha = pulse),
      radius = 2.5.dp.toPx(),
      center = Offset(startX + cos(wavePhase) * 6f, pY)
    )
  }

  // Temperature warning badge: "Poor Flow / 32°C"
  drawRoundRect(
    color = Color(0xFFEF4444),
    topLeft = Offset(acLeft + unit * 0.5f, acTop + unit * 0.4f),
    size = Size(unit * 1.8f, unit * 0.8f),
    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
  )
}

/**
 * 3D Leaking Tap / Washroom Plumbing: Copper pipe, faucet & animated dripping drops with ripples
 */
private fun DrawScope.draw3DLeakingTap(
  cx: Float,
  cy: Float,
  w: Float,
  h: Float,
  flow: Float
) {
  val unit = w / 10f

  // Copper Wall Pipe
  val pipeColor = Color(0xFFD97706)
  drawLine(pipeColor, Offset(cx - unit * 2.4f, cy - unit * 1.8f), Offset(cx, cy - unit * 1.8f), strokeWidth = 8.dp.toPx())

  // Tap Spout curving down
  val spoutColor = Color(0xFFF59E0B)
  drawLine(spoutColor, Offset(cx, cy - unit * 1.8f), Offset(cx, cy - unit * 0.8f), strokeWidth = 7.dp.toPx(), cap = StrokeCap.Round)

  // Valve Wheel on top
  drawRoundRect(
    Color(0xFFDC2626),
    Offset(cx - unit * 0.8f, cy - unit * 2.5f),
    Size(unit * 1.6f, unit * 0.5f),
    CornerRadius(2.dp.toPx(), 2.dp.toPx())
  )

  // Animated Falling Water Droplet
  val dropY = (cy - unit * 0.8f) + flow * (unit * 2.2f)
  if (flow < 0.85f) {
    drawCircle(
      color = Color(0xFF38BDF8),
      radius = 4.dp.toPx(),
      center = Offset(cx, dropY)
    )
  }

  // Ground Water Ripples (Expanding concentric circles)
  val groundY = cy + unit * 1.5f
  val rippleRadius = (flow * unit * 1.6f)
  val rippleAlpha = (1f - flow).coerceIn(0f, 1f)

  drawOval(
    color = Color(0xFF38BDF8).copy(alpha = rippleAlpha),
    topLeft = Offset(cx - rippleRadius, groundY - rippleRadius * 0.35f),
    size = Size(rippleRadius * 2f, rippleRadius * 0.7f),
    style = Stroke(width = 1.5.dp.toPx())
  )
}

/**
 * 3D Broken Light / Sockets: Fluorescent fixture with flickering tube & spark
 */
private fun DrawScope.draw3DBrokenLight(
  cx: Float,
  cy: Float,
  w: Float,
  h: Float,
  pulse: Float
) {
  val unit = w / 10f

  // Ceiling Light Frame
  drawRoundRect(
    Color(0xFF334155),
    Offset(cx - unit * 3f, cy - unit * 2.0f),
    Size(unit * 6f, unit * 1.4f),
    CornerRadius(4.dp.toPx(), 4.dp.toPx())
  )

  // Fluorescent Tube Left (Working)
  drawRoundRect(
    Color(0xFFE2E8F0),
    Offset(cx - unit * 2.6f, cy - unit * 1.7f),
    Size(unit * 2.2f, unit * 0.6f),
    CornerRadius(3.dp.toPx(), 3.dp.toPx())
  )

  // Fluorescent Tube Right (Flickering Broken)
  val flickerColor = if (pulse > 0.5f) Color(0xFFFACC15).copy(alpha = pulse) else Color(0xFF475569)
  drawRoundRect(
    flickerColor,
    Offset(cx + unit * 0.4f, cy - unit * 1.7f),
    Size(unit * 2.2f, unit * 0.6f),
    CornerRadius(3.dp.toPx(), 3.dp.toPx())
  )

  // Sparks Arc
  if (pulse > 0.6f) {
    drawLine(
      Color(0xFFFDE047),
      Offset(cx + unit * 0.3f, cy - unit * 1.4f),
      Offset(cx + unit * 0.7f, cy - unit * 0.8f),
      strokeWidth = 2.dp.toPx()
    )
  }
}

/**
 * 3D General Maintenance Warning Cube
 */
private fun DrawScope.draw3DGeneralWarning(
  cx: Float,
  cy: Float,
  w: Float,
  h: Float,
  pulse: Float
) {
  val unit = w / 10f
  val triPath = Path().apply {
    moveTo(cx, cy - unit * 2.4f)
    lineTo(cx + unit * 2.2f, cy + unit * 1.0f)
    lineTo(cx - unit * 2.2f, cy + unit * 1.0f)
    close()
  }
  drawPath(triPath, Color(0xFFF59E0B))

  // Exclamation
  drawLine(
    Color.Black,
    Offset(cx, cy - unit * 1.4f),
    Offset(cx, cy),
    strokeWidth = 3.dp.toPx(),
    cap = StrokeCap.Round
  )
  drawCircle(Color.Black, 2.5.dp.toPx(), Offset(cx, cy + unit * 0.5f))
}

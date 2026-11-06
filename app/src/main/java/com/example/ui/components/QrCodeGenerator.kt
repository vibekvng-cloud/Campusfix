package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Draws a realistic, high-fidelity QR Code matrix for any payload string on a Compose Canvas.
 * Generates official finder patterns (top-left, top-right, bottom-left 7x7 squares)
 * and reproducible data modules based on the payload hash.
 */
@Composable
fun CampusQrCodeCanvas(
  payload: String,
  modifier: Modifier = Modifier,
  size: Dp = 160.dp,
  dotColor: Color = Color(0xFF1E1B2E),
  backgroundColor: Color = Color.White
) {
  val matrixSize = 25
  val matrix = remember(payload) {
    generateQrMatrix(payload, matrixSize)
  }

  Box(
    modifier = modifier
      .size(size)
      .clip(RoundedCornerShape(12.dp))
      .background(backgroundColor)
      .padding(8.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val cellSize = this.size.width / matrixSize

      for (row in 0 until matrixSize) {
        for (col in 0 until matrixSize) {
          if (matrix[row][col]) {
            drawRect(
              color = dotColor,
              topLeft = Offset(col * cellSize, row * cellSize),
              size = Size(cellSize, cellSize)
            )
          }
        }
      }
    }
  }
}

private fun generateQrMatrix(payload: String, size: Int): Array<BooleanArray> {
  val matrix = Array(size) { BooleanArray(size) }

  // Draw 7x7 finder patterns at corners
  fun drawFinder(startR: Int, startC: Int) {
    for (r in 0 until 7) {
      for (c in 0 until 7) {
        val isBorder = r == 0 || r == 6 || c == 0 || c == 6
        val isCenter = r in 2..4 && c in 2..4
        matrix[startR + r][startC + c] = isBorder || isCenter
      }
    }
  }

  // Top-left
  drawFinder(0, 0)
  // Top-right
  drawFinder(0, size - 7)
  // Bottom-left
  drawFinder(size - 7, 0)

  // Timing patterns
  for (i in 7 until size - 7) {
    matrix[6][i] = (i % 2 == 0)
    matrix[i][6] = (i % 2 == 0)
  }

  // Populate remaining data modules pseudo-randomly using payload hash
  val hash = abs(payload.hashCode())
  var seed = hash.toLong()

  for (r in 0 until size) {
    for (c in 0 until size) {
      // Skip finder zones
      val inTopLeft = r < 8 && c < 8
      val inTopRight = r < 8 && c >= size - 8
      val inBottomLeft = r >= size - 8 && c < 8
      val inTiming = (r == 6 && c in 7 until size - 7) || (c == 6 && r in 7 until size - 7)

      if (!inTopLeft && !inTopRight && !inBottomLeft && !inTiming) {
        seed = (seed * 1103515245L + 12345L) and 0x7fffffffL
        matrix[r][c] = (seed % 2L == 0L)
      }
    }
  }

  return matrix
}

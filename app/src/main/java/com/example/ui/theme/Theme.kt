package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.material3.lightColorScheme

private val SkillAlleyColorScheme = lightColorScheme(
  primary = SkillAlleyButtonDark,
  onPrimary = Color.White,
  primaryContainer = SkillAlleyCard,
  onPrimaryContainer = SkillAlleyTextPrimary,
  secondary = Color(0xFF1E3A8A),
  onSecondary = Color.White,
  secondaryContainer = SkillAlleyPillInactive,
  onSecondaryContainer = SkillAlleyTextPrimary,
  tertiary = UccGold,
  background = SkillAlleyBg,
  onBackground = SkillAlleyTextPrimary,
  surface = SkillAlleyCard,
  onSurface = SkillAlleyTextPrimary,
  surfaceVariant = SkillAlleyBg,
  onSurfaceVariant = SkillAlleyTextSecondary,
  outline = SkillAlleyBorder
)

@Composable
fun CampusFixTheme(
  content: @Composable () -> Unit
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = Color.Transparent.toArgb()
      window.navigationBarColor = Color.Transparent.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
      WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
    }
  }

  MaterialTheme(
    colorScheme = SkillAlleyColorScheme,
    typography = Typography,
    content = content
  )
}

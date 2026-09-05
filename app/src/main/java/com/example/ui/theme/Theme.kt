package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = SleekCyan,
  onPrimary = Color(0xFF0F172A),
  primaryContainer = SleekSurface,
  onPrimaryContainer = SleekCyan,
  secondary = SleekGreen,
  onSecondary = Color(0xFF022C22),
  secondaryContainer = Color(0xFF064E3B),
  onSecondaryContainer = Color(0xFFA7F3D0),
  tertiary = SleekYellowEnergy,
  onTertiary = Color(0xFF422006),
  tertiaryContainer = Color(0xFF713F12),
  onTertiaryContainer = Color(0xFFFEF08A),
  background = SleekBg,
  onBackground = SleekTextPrimary,
  surface = SleekSurface,
  onSurface = SleekTextPrimary,
  surfaceVariant = SleekSurfaceHeader,
  onSurfaceVariant = SleekTextSecondary,
  outline = SleekBorder,
  outlineVariant = SleekBorderMuted,
  error = SleekRedLaser,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = SleekCyanDark,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE0F2FE),
  onPrimaryContainer = Color(0xFF0369A1),
  secondary = Color(0xFF059669),
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFD1FAE5),
  onSecondaryContainer = Color(0xFF065F46),
  tertiary = Color(0xFFD97706),
  onTertiary = Color.White,
  tertiaryContainer = Color(0xFFFEF3C7),
  onTertiaryContainer = Color(0xFF78350F),
  background = LabLightBg,
  onBackground = LabLightText,
  surface = LabLightSurface,
  onSurface = LabLightText,
  surfaceVariant = LabLightSurfaceVariant,
  onSurfaceVariant = LabLightSubtext,
  outline = LabLightBorder,
  error = Color(0xFFDC2626),
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep laboratory brand colors distinctive
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = TokBookBlue,
  onPrimary = Color.White,
  secondary = TokBookPink,
  onSecondary = Color.White,
  tertiary = TokBookCyan,
  onTertiary = Color.Black,
  background = DarkBg,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkCard,
  onSurfaceVariant = DarkTextSecondary,
  outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
  primary = TokBookBlue,
  onPrimary = Color.White,
  secondary = TokBookPink,
  onSecondary = Color.White,
  tertiary = TokBookPurple,
  onTertiary = Color.White,
  background = LightBg,
  onBackground = LightTextPrimary,
  surface = LightSurface,
  onSurface = LightTextPrimary,
  surfaceVariant = LightCard,
  onSurfaceVariant = LightTextSecondary,
  outline = LightBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our curated palette for distinctive TokBook branding
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // Social media reels look best in dark mode default

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

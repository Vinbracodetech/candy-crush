package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CandyGlassColorScheme = darkColorScheme(
    primary = NeonPinkPrimary,
    onPrimary = Color.White,
    secondary = NeonCyanSecondary,
    onSecondary = Color(0xFF002026),
    tertiary = NeonGoldTertiary,
    onTertiary = Color(0xFF261C00),
    background = DeepCosmicDark,
    onBackground = TextPrimary,
    surface = CosmicPurpleMid,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF2B154D),
    onSurfaceVariant = TextSecondary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CandyGlassColorScheme,
        typography = Typography,
        content = content
    )
}

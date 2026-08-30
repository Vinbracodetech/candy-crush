package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// Frosted Glass & Vibrant Candy Color System
// ==========================================

// Core Dark & Midnight Frosted Canvas Colors
val DeepCosmicDark = Color(0xFF0D061A)
val CosmicPurpleMid = Color(0xFF1E0E38)
val CosmicBlueNavy = Color(0xFF09142B)
val FrostedGlassSurface = Color(0x2EFFFFFF)
val FrostedGlassSurfaceDark = Color(0xD9130728)
val FrostedGlassSurfaceLight = Color(0x3DFFFFFF)

// Vibrant Neon & Candy Accent Palette
val NeonPinkPrimary = Color(0xFFFF2A85)
val NeonCyanSecondary = Color(0xFF00F0FF)
val NeonGoldTertiary = Color(0xFFFFD000)
val NeonGreenAccent = Color(0xFF00FF88)
val NeonPurpleAccent = Color(0xFF9D4EDD)
val NeonCoralAccent = Color(0xFFFF5252)

// Frosted Glass Translucency Tokens
val GlassFrostWhite = Color(0x2EFFFFFF)
val GlassFrostWhiteHeavy = Color(0x4DFFFFFF)
val GlassFrostWhiteSubtle = Color(0x14FFFFFF)
val GlassFrostDark = Color(0x800B0418)

val GlassBorderGradient = listOf(
    Color(0xB3FFFFFF),
    Color(0x33FFFFFF),
    Color(0x6600F0FF),
    Color(0x80FF2A85)
)

val FrostedGlassBorderBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xCCFFFFFF),
        Color(0x33FFFFFF),
        Color(0x8000F0FF),
        Color(0x66FF2A85)
    )
)

// Gradients & Brushes
val BackgroundCosmicGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF130726),
        Color(0xFF220C42),
        Color(0xFF0A1B36),
        Color(0xFF080414)
    )
)

val CardGlassBrush = Brush.linearGradient(
    colors = listOf(
        Color(0x38FFFFFF),
        Color(0x1AFFFFFF),
        Color(0x0EFFFFFF)
    )
)

val ButtonPinkGlassBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFFF2A85),
        Color(0xFFFF609E)
    )
)

val ButtonCyanGlassBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF00D2FF),
        Color(0xFF00F0FF),
        Color(0xFF80FFEA)
    )
)

val ButtonGoldGlassBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFFF9100),
        Color(0xFFFFD000),
        Color(0xFFFFE57F)
    )
)

val ButtonPurpleGlassBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF7928CA),
        Color(0xFF9D4EDD),
        Color(0xFFC77DFF)
    )
)

val ButtonGreenGlassBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF00C853),
        Color(0xFF00FF88)
    )
)

// Typography & Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFE5DDF8)
val TextMuted = Color(0xFFA59BC6)
val TextIce = Color(0xFFE0F7FA)


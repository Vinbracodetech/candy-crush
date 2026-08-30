package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ButtonPinkGlassBrush
import com.example.ui.theme.DeepCosmicDark
import com.example.ui.theme.NeonCyanSecondary
import com.example.ui.theme.NeonGoldTertiary
import com.example.ui.theme.NeonPinkPrimary
import com.example.ui.theme.NeonPurpleAccent

@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "frosted_orbs")
    val orb1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1"
    )
    val orb2Offset by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2"
    )
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepCosmicDark)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Deep multi-stop frosted dark backdrop
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF140827),
                        Color(0xFF220E40),
                        Color(0xFF0C1935),
                        Color(0xFF080414)
                    )
                )
            )

            // Dynamic luminous crystal orb 1 (Magenta Pink)
            val orb1X = w * (0.15f + 0.65f * orb1Offset)
            val orb1Y = h * (0.12f + 0.42f * (1f - orb1Offset))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonPinkPrimary.copy(alpha = 0.38f),
                        NeonPinkPrimary.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(orb1X, orb1Y),
                    radius = w * 0.75f
                ),
                radius = w * 0.75f,
                center = Offset(orb1X, orb1Y)
            )

            // Dynamic luminous crystal orb 2 (Cyan Ice)
            val orb2X = w * (0.85f - 0.6f * orb2Offset)
            val orb2Y = h * (0.68f - 0.35f * orb2Offset)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonCyanSecondary.copy(alpha = 0.32f),
                        NeonCyanSecondary.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(orb2X, orb2Y),
                    radius = w * 0.7f
                ),
                radius = w * 0.7f,
                center = Offset(orb2X, orb2Y)
            )

            // Orb 3 (Vibrant Violet Center-Base)
            val orb3X = w * 0.5f
            val orb3Y = h * 0.92f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonPurpleAccent.copy(alpha = 0.28f),
                        Color(0xFFFFD000).copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(orb3X, orb3Y),
                    radius = w * 0.6f
                ),
                radius = w * 0.6f,
                center = Offset(orb3X, orb3Y)
            )

            // Fine frosted particle sparkle specks
            drawCircle(
                color = Color.White.copy(alpha = shimmerAlpha),
                radius = 3.5f,
                center = Offset(w * 0.25f, h * 0.22f)
            )
            drawCircle(
                color = NeonCyanSecondary.copy(alpha = shimmerAlpha),
                radius = 4f,
                center = Offset(w * 0.78f, h * 0.38f)
            )
            drawCircle(
                color = NeonGoldTertiary.copy(alpha = shimmerAlpha),
                radius = 3f,
                center = Offset(w * 0.45f, h * 0.75f)
            )
        }

        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color(0x28FFFFFF),
    borderColor: Color = Color(0x66FFFFFF),
    borderWidth: Dp = 1.2.dp,
    elevation: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0x66000000),
                spotColor = Color(0x6600F0FF)
            )
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = (backgroundColor.alpha * 0.55f).coerceIn(0f, 1f)),
                        backgroundColor.copy(alpha = (backgroundColor.alpha * 0.25f).coerceIn(0f, 1f))
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(500f, 500f)
                )
            )
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.18f),
                        borderColor.copy(alpha = 0.65f),
                        Color(0x3300F0FF)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(300f, 400f)
                ),
                shape = shape
            )
    ) {
        // Specular top highlight line reflecting frosted glass edge
        Canvas(modifier = Modifier.matchParentSize()) {
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.55f),
                        Color.White.copy(alpha = 0.15f),
                        Color.Transparent
                    )
                ),
                start = Offset(16f, 1.2f),
                end = Offset(size.width - 16f, 1.2f),
                strokeWidth = 2.2f
            )
        }
        content()
    }
}

@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    brush: Brush = ButtonPinkGlassBrush,
    shape: Shape = RoundedCornerShape(16.dp),
    enabled: Boolean = true,
    padding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.94f else 1f

    Box(
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .shadow(
                elevation = if (enabled) 10.dp else 2.dp,
                shape = shape,
                spotColor = if (enabled) NeonPinkPrimary else Color.Transparent
            )
            .clip(shape)
            .background(
                brush = if (enabled) brush else Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x1AFFFFFF)))
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.85f),
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.65f)
                    )
                ),
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = Color.White),
                onClick = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    onClick()
                }
            )
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        // Subtle glossy specular sheen over button top half
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.28f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = size.height * 0.55f
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun GlassProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    fillBrush: Brush = Brush.horizontalGradient(
        colors = listOf(
            NeonCyanSecondary,
            NeonPinkPrimary,
            NeonGoldTertiary
        )
    ),
    trackColor: Color = Color(0x40000000),
    height: Dp = 14.dp,
    shape: Shape = RoundedCornerShape(10.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sheen")
    val sheenOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sheenOffset"
    )

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(shape)
            .background(trackColor)
            .border(1.dp, Color.White.copy(alpha = 0.3f), shape)
    ) {
        val clamped = progress.coerceIn(0f, 1f)
        if (clamped > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize(clamped)
                    .clip(shape)
                    .background(fillBrush)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val sheenWidth = size.width * 0.45f
                    val startX = size.width * sheenOffset
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.5f),
                                Color.Transparent
                            ),
                            startX = startX,
                            endX = startX + sheenWidth
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun GlassBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = NeonCyanSecondary,
    backgroundColor: Color = Color(0x38000000)
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, color.copy(alpha = 0.75f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FrostedGlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    backgroundColor: Color = Color(0x28FFFFFF),
    borderColor: Color = Color(0x66FFFFFF),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.92f else 1f

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

typealias RowScope = androidx.compose.foundation.layout.RowScope


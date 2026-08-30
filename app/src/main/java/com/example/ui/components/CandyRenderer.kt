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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.data.model.CandyColor
import com.example.data.model.CandyTile
import com.example.data.model.SpecialType
import com.example.data.model.TileObstacle
import com.example.ui.theme.NeonGoldTertiary

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

@Composable
fun CandyTileView(
    tile: CandyTile?,
    obstacle: TileObstacle,
    isSelected: Boolean,
    onClick: () -> Unit,
    onSwipe: (Float, Float) -> Unit = {_,_->},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "candy_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    val scaleModifier = when {
        isSelected -> Modifier.scale(1.12f)
        tile?.isHinted == true -> Modifier.scale(pulseScale)
        else -> Modifier
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(1.5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x22FFFFFF),
                        Color(0x10FFFFFF),
                        Color(0x08FFFFFF)
                    )
                )
            )
            .border(
                width = if (isSelected) 2.dp else 0.8.dp,
                brush = if (isSelected) Brush.linearGradient(listOf(NeonGoldTertiary, Color.White))
                else Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White),
                onClick = onClick
            )
            .pointerInput(Unit) {
                var totalDragX = 0f
                var totalDragY = 0f
                detectDragGestures(
                    onDragStart = { 
                        totalDragX = 0f
                        totalDragY = 0f
                        onClick() 
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y
                        if (abs(totalDragX) > 50f || abs(totalDragY) > 50f) {
                            onSwipe(totalDragX, totalDragY)
                            totalDragX = 0f
                            totalDragY = 0f
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Draw Obstacle (Jelly / Chocolate) background or overlay
        if (obstacle != TileObstacle.NONE) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawObstacleBackground(obstacle)
            }
        }

        // Draw Candy Tile if present
        if (tile != null) {
            Canvas(modifier = Modifier.fillMaxSize().then(scaleModifier)) {
                if (tile.isIngredient) {
                    drawIngredientCherry()
                } else {
                    drawCandyGem(tile.color, tile.special, spinAngle)
                }
            }
        }

        // Draw Foreground Obstacle (Cookie / Chocolate)
        if (obstacle == TileObstacle.CHOCOLATE_BLOCK || obstacle == TileObstacle.COOKIE_BLOCK) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawBlockerForeground(obstacle)
            }
        }
    }
}

private fun DrawScope.drawObstacleBackground(obstacle: TileObstacle) {
    val w = size.width
    val h = size.height
    when (obstacle) {
        TileObstacle.JELLY_SINGLE -> {
            // Single layer frosted ice jelly
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0x8080D8FF), Color(0x500091EA), Color(0x80FFFFFF)),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                ),
                cornerRadius = CornerRadius(12f, 12f)
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.5f),
                style = Stroke(width = 2.5f),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }
        TileObstacle.JELLY_DOUBLE -> {
            // Double layer thick icy jelly
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xCC00E5FF), Color(0x990052D4), Color(0xCCB388FF)),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                ),
                cornerRadius = CornerRadius(12f, 12f)
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.8f),
                style = Stroke(width = 3.5f),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }
        else -> {}
    }
}

private fun DrawScope.drawBlockerForeground(obstacle: TileObstacle) {
    val w = size.width
    val h = size.height
    val pad = w * 0.08f
    val chocoColor = if (obstacle == TileObstacle.CHOCOLATE_BLOCK) Color(0xFF4E342E) else Color(0xFF8D6E63)
    val lightChoco = if (obstacle == TileObstacle.CHOCOLATE_BLOCK) Color(0xFF6D4C41) else Color(0xFFA1887F)

    // Chocolate wafer block
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(lightChoco, chocoColor),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        ),
        topLeft = Offset(pad, pad),
        size = Size(w - pad * 2, h - pad * 2),
        cornerRadius = CornerRadius(10f, 10f)
    )

    // Inner embossed wafer grid
    val innerPad = pad + w * 0.12f
    drawRoundRect(
        color = Color(0x33000000),
        topLeft = Offset(innerPad, innerPad),
        size = Size(w - innerPad * 2, h - innerPad * 2),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.25f),
        topLeft = Offset(innerPad, innerPad),
        size = Size(w - innerPad * 2, h - innerPad * 2),
        style = Stroke(width = 2f),
        cornerRadius = CornerRadius(6f, 6f)
    )
}

private fun DrawScope.drawIngredientCherry() {
    val cx = size.width * 0.5f
    val cy = size.height * 0.55f
    val r = size.width * 0.28f

    // Stem
    val stemPath = Path().apply {
        moveTo(cx, cy - r * 0.7f)
        cubicTo(cx + r * 0.6f, cy - r * 1.5f, cx - r * 0.4f, cy - r * 1.8f, cx, cy - r * 1.9f)
    }
    drawPath(stemPath, color = Color(0xFF7CB342), style = Stroke(width = 4f))

    // Leaf
    val leafPath = Path().apply {
        moveTo(cx, cy - r * 1.6f)
        quadraticTo(cx + r * 0.7f, cy - r * 1.7f, cx + r * 0.6f, cy - r * 1.3f)
        quadraticTo(cx + r * 0.2f, cy - r * 1.4f, cx, cy - r * 1.6f)
    }
    drawPath(leafPath, color = Color(0xFF4CAF50))

    // Cherry Body (Glossy Red Gem)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFF5252), Color(0xFFD50000), Color(0xFF5A0000)),
            center = Offset(cx - r * 0.3f, cy - r * 0.3f),
            radius = r * 1.2f
        ),
        radius = r,
        center = Offset(cx, cy)
    )

    // Specular Highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.75f),
        radius = r * 0.25f,
        center = Offset(cx - r * 0.35f, cy - r * 0.35f)
    )
}

private fun DrawScope.drawCandyGem(color: CandyColor, special: SpecialType, spinAngle: Float) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.5f
    val w = size.width
    val h = size.height
    val radius = w * 0.36f

    when (special) {
        SpecialType.COLOR_BOMB -> {
            // Rainbow Disco Chocolate Sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF5D4037), Color(0xFF3E2723), Color(0xFF1B0000)),
                    center = Offset(cx - radius * 0.3f, cy - radius * 0.3f),
                    radius = radius * 1.2f
                ),
                radius = radius,
                center = Offset(cx, cy)
            )
            // Glowing Rainbow Orbit Ring
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Magenta, Color.Red
                    ),
                    center = Offset(cx, cy)
                ),
                radius = radius * 1.05f,
                center = Offset(cx, cy),
                style = Stroke(width = 4.5f)
            )
            // Multi-colored sprinkles
            val sprinkleColors = listOf(Color.Yellow, Color.Cyan, Color.Magenta, Color.White, Color.Green)
            for (i in 0 until 6) {
                val ang = Math.toRadians((i * 60 + spinAngle).toDouble())
                val sx = cx + (radius * 0.5f * Math.cos(ang)).toFloat()
                val sy = cy + (radius * 0.5f * Math.sin(ang)).toFloat()
                drawCircle(color = sprinkleColors[i % sprinkleColors.size], radius = 3.5f, center = Offset(sx, sy))
            }
            return
        }
        else -> {}
    }

    // Standard 3D Glossy Candy Gem
    when (color) {
        CandyColor.RED -> {
            // Heart/Diamond Gem
            val path = Path().apply {
                moveTo(cx, cy - radius * 0.8f)
                lineTo(cx + radius * 0.9f, cy)
                lineTo(cx, cy + radius * 0.95f)
                lineTo(cx - radius * 0.9f, cy)
                close()
            }
            drawPath(
                path,
                brush = Brush.linearGradient(
                    colors = listOf(color.accentColor, color.baseColor, Color(0xFF880020)),
                    start = Offset(cx - radius, cy - radius),
                    end = Offset(cx + radius, cy + radius)
                )
            )
            drawPath(path, color = Color.White.copy(alpha = 0.5f), style = Stroke(width = 2f))
        }

        CandyColor.ORANGE -> {
            // Rounded Hexagon
            val path = Path().apply {
                for (i in 0 until 6) {
                    val angle = Math.toRadians((i * 60 - 30).toDouble())
                    val px = cx + (radius * 0.88f * Math.cos(angle)).toFloat()
                    val py = cy + (radius * 0.88f * Math.sin(angle)).toFloat()
                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                }
                close()
            }
            drawPath(
                path,
                brush = Brush.radialGradient(
                    colors = listOf(color.accentColor, color.baseColor, Color(0xFFB34700)),
                    center = Offset(cx - radius * 0.3f, cy - radius * 0.3f),
                    radius = radius * 1.1f
                )
            )
            drawPath(path, color = Color.White.copy(alpha = 0.5f), style = Stroke(width = 2f))
        }

        CandyColor.YELLOW -> {
            // Sparkling Star Gem
            val path = Path().apply {
                for (i in 0 until 8) {
                    val r = if (i % 2 == 0) radius * 0.92f else radius * 0.52f
                    val angle = Math.toRadians((i * 45).toDouble())
                    val px = cx + (r * Math.cos(angle)).toFloat()
                    val py = cy + (r * Math.sin(angle)).toFloat()
                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                }
                close()
            }
            drawPath(
                path,
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, color.baseColor, Color(0xFFB28900)),
                    center = Offset(cx - radius * 0.2f, cy - radius * 0.2f),
                    radius = radius * 1.1f
                )
            )
            drawPath(path, color = Color.White.copy(alpha = 0.6f), style = Stroke(width = 2f))
        }

        CandyColor.GREEN -> {
            // Square Cushion Gem
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(color.accentColor, color.baseColor, Color(0xFF00703C)),
                    start = Offset(cx - radius, cy - radius),
                    end = Offset(cx + radius, cy + radius)
                ),
                topLeft = Offset(cx - radius * 0.75f, cy - radius * 0.75f),
                size = Size(radius * 1.5f, radius * 1.5f),
                cornerRadius = CornerRadius(14f, 14f)
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.5f),
                topLeft = Offset(cx - radius * 0.75f, cy - radius * 0.75f),
                size = Size(radius * 1.5f, radius * 1.5f),
                cornerRadius = CornerRadius(14f, 14f),
                style = Stroke(width = 2f)
            )
        }

        CandyColor.BLUE -> {
            // Teardrop / Sphere Jewel
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color.accentColor, color.baseColor, Color(0xFF004BA0)),
                    center = Offset(cx - radius * 0.35f, cy - radius * 0.35f),
                    radius = radius * 1.15f
                ),
                radius = radius * 0.85f,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.45f),
                radius = radius * 0.85f,
                center = Offset(cx, cy),
                style = Stroke(width = 2f)
            )
        }

        CandyColor.PURPLE -> {
            // Triangle / Trilliant Gem
            val path = Path().apply {
                moveTo(cx, cy - radius * 0.9f)
                lineTo(cx + radius * 0.85f, cy + radius * 0.75f)
                lineTo(cx - radius * 0.85f, cy + radius * 0.75f)
                close()
            }
            drawPath(
                path,
                brush = Brush.linearGradient(
                    colors = listOf(color.accentColor, color.baseColor, Color(0xFF5C007A)),
                    start = Offset(cx - radius, cy - radius),
                    end = Offset(cx + radius, cy + radius)
                )
            )
            drawPath(path, color = Color.White.copy(alpha = 0.5f), style = Stroke(width = 2f))
        }
    }

    // Top-left Specular Glass Highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.7f),
        radius = radius * 0.22f,
        center = Offset(cx - radius * 0.35f, cy - radius * 0.35f)
    )

    // Special Candies Overlays
    when (special) {
        SpecialType.STRIPED_HORIZONTAL -> {
            for (dy in listOf(-0.4f, 0f, 0.4f)) {
                drawLine(
                    color = Color.White.copy(alpha = 0.9f),
                    start = Offset(cx - radius * 0.8f, cy + radius * dy),
                    end = Offset(cx + radius * 0.8f, cy + radius * dy),
                    strokeWidth = 3.5f
                )
            }
        }
        SpecialType.STRIPED_VERTICAL -> {
            for (dx in listOf(-0.4f, 0f, 0.4f)) {
                drawLine(
                    color = Color.White.copy(alpha = 0.9f),
                    start = Offset(cx + radius * dx, cy - radius * 0.8f),
                    end = Offset(cx + radius * dx, cy + radius * 0.8f),
                    strokeWidth = 3.5f
                )
            }
        }
        SpecialType.WRAPPED_BOMB -> {
            // Explosive halo wrapper
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = radius * 0.95f,
                center = Offset(cx, cy),
                style = Stroke(width = 3f)
            )
            // Mini corners
            val pad = radius * 0.75f
            drawLine(Color.Yellow, Offset(cx - pad, cy - pad), Offset(cx - pad * 1.3f, cy - pad * 1.3f), 4f)
            drawLine(Color.Yellow, Offset(cx + pad, cy - pad), Offset(cx + pad * 1.3f, cy - pad * 1.3f), 4f)
            drawLine(Color.Yellow, Offset(cx - pad, cy + pad), Offset(cx - pad * 1.3f, cy + pad * 1.3f), 4f)
            drawLine(Color.Yellow, Offset(cx + pad, cy + pad), Offset(cx + pad * 1.3f, cy + pad * 1.3f), 4f)
        }
        else -> {}
    }
}

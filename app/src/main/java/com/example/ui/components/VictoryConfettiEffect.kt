package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotationSpeed: Float,
    val size: Float,
    val color: Color,
    val isCircle: Boolean,
    val delayMs: Int
)

@Composable
fun VictoryConfettiEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 110,
    durationMs: Int = 3000
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMs, easing = LinearEasing)
        )
    }

    val palette = remember {
        listOf(
            Color(0xFFFF2A85), // Neon Pink
            Color(0xFF00E5FF), // Neon Cyan
            Color(0xFFFFD000), // Neon Gold
            Color(0xFF00E676), // Neon Green
            Color(0xFFD500F9), // Purple
            Color(0xFFFF7A00), // Orange
            Color(0xFFFFFFFF), // Sparkling White
            Color(0xFFFF5252)  // Strawberry Red
        )
    }

    val particles = remember {
        List(particleCount) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2f
            val speed = Random.nextFloat() * 450f + 250f
            ConfettiParticle(
                initialX = Random.nextFloat() * 0.8f + 0.1f, // 10% to 90% screen width
                initialY = Random.nextFloat() * 0.25f + 0.1f, // Emerge from upper portion
                velocityX = cos(angle) * speed,
                velocityY = sin(angle) * speed - 200f, // Upward initial burst
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                size = Random.nextFloat() * 12f + 8f,
                color = palette.random(),
                isCircle = Random.nextBoolean(),
                delayMs = Random.nextInt(400)
            )
        }
    }

    val currentProgress = progress.value

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        for (p in particles) {
            val adjustedProgress = ((currentProgress * durationMs - p.delayMs) / (durationMs - p.delayMs)).coerceIn(0f, 1f)
            if (adjustedProgress <= 0f) continue

            val timeSec = adjustedProgress * (durationMs / 1000f)
            // Physics: x = x0 + vx*t, y = y0 + vy*t + 0.5*g*t^2
            val gravity = 900f
            val px = p.initialX * w + p.velocityX * timeSec * 0.6f
            val py = p.initialY * h + p.velocityY * timeSec * 0.6f + 0.5f * gravity * timeSec * timeSec
            val rot = p.rotationSpeed * timeSec
            val alpha = (1f - adjustedProgress * 0.9f).coerceIn(0f, 1f)

            if (py < h + 50f && px > -50f && px < w + 50f) {
                rotate(rot, pivot = Offset(px, py)) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color.copy(alpha = alpha),
                            radius = p.size * 0.5f,
                            center = Offset(px, py)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(px - p.size * 0.5f, py - p.size * 0.35f),
                            size = Size(p.size, p.size * 0.7f)
                        )
                    }
                }
            }
        }
    }
}

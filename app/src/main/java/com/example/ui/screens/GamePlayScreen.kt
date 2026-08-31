package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ads.RewardReason
import com.example.data.local.PlayerProfile
import com.example.data.model.GoalType
import com.example.data.model.LevelConfig
import com.example.data.model.TileObstacle
import com.example.engine.MatchEngineState
import com.example.ui.components.CandyTileView
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassProgressBar
import com.example.ui.theme.ButtonCyanGlassBrush
import com.example.ui.theme.ButtonGoldGlassBrush
import com.example.ui.theme.ButtonPinkGlassBrush
import com.example.ui.theme.NeonCyanSecondary
import com.example.ui.theme.NeonGoldTertiary
import com.example.ui.theme.NeonGreenAccent
import com.example.ui.theme.NeonPinkPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GamePlayScreen(
    levelConfig: LevelConfig,
    engineState: MatchEngineState,
    profile: PlayerProfile,
    onTileClick: (Int, Int) -> Unit,
    onSelectBooster: (String) -> Unit,
    onWatchAdForExtraMoves: () -> Unit,
    onNextLevel: () -> Unit,
    onRetryLevel: () -> Unit,
    onExitToMap: () -> Unit,
    onToggleSound: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPaused by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 12.dp, end = 12.dp, bottom = 65.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top HUD Bar (Score, Moves, Goals, Stars, Pause)
            GameTopHud(
                config = levelConfig,
                state = engineState,
                profile = profile,
                onPause = { isPaused = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Main Match-3 Board Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Glass Board Frame
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    backgroundColor = Color(0x3514082B),
                    borderColor = NeonCyanSecondary.copy(alpha = 0.5f),
                    borderWidth = 1.5.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    ) {
                        val grid = engineState.grid
                        for (r in grid.indices) {
                            Row(modifier = Modifier.weight(1f)) {
                                for (c in grid[r].indices) {
                                    val tile = grid[r][c]
                                    val obstacle = engineState.obstacles[r to c] ?: TileObstacle.NONE
                                    val isSelected = engineState.selectedTile == (r to c)

                                    CandyTileView(
                                        tile = tile,
                                        obstacle = obstacle,
                                        isSelected = isSelected,
                                        onClick = { onTileClick(r, c) },
                                        onSwipe = { dx, dy ->
                                            if (kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
                                                if (dx > 0) onTileClick(r, c + 1) else onTileClick(r, c - 1)
                                            } else {
                                                if (dy > 0) onTileClick(r + 1, c) else onTileClick(r - 1, c)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Floating Particle Bursts
                    Canvas(modifier = Modifier.matchParentSize()) {
                        for (p in engineState.particles) {
                            val elapsed = System.currentTimeMillis() - p.createdAt
                            val progress = (elapsed.toFloat() / p.lifetimeMs.toFloat()).coerceIn(0f, 1f)
                            if (progress < 1f) {
                                val currentX = (p.x + 0.5f) * (size.width / 8f) + p.vx * progress * 30f
                                val currentY = (p.y + 0.5f) * (size.height / 8f) + p.vy * progress * 30f
                                val alpha = (1f - progress)
                                drawCircle(
                                    color = p.color.copy(alpha = alpha),
                                    radius = p.size * (1f - progress * 0.5f),
                                    center = Offset(currentX, currentY)
                                )
                            }
                        }
                    }

                    // Animated Combo Toast Banner
                    engineState.lastComboMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .shadow(24.dp, RoundedCornerShape(16.dp), spotColor = NeonPinkPrimary)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.horizontalGradient(listOf(NeonPinkPrimary, Color(0xFF7928CA))))
                                .border(2.dp, Color.White, RoundedCornerShape(16.dp))
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = msg,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom In-Game Booster Bar
            InGameBoosterBar(
                profile = profile,
                activeBooster = engineState.activeBooster,
                onSelectBooster = onSelectBooster,
                onWatchAdForExtraMoves = onWatchAdForExtraMoves
            )

            Spacer(modifier = Modifier.height(72.dp)) // Space for bottom banner ad
        }

        // Victory Dialog
        if (engineState.isGameWon) {
            LevelWinDialog(
                levelNumber = levelConfig.levelNumber,
                score = engineState.currentScore,
                targetScore1 = levelConfig.targetScore1Star,
                targetScore2 = levelConfig.targetScore2Star,
                targetScore3 = levelConfig.targetScore3Star,
                onNextLevel = onNextLevel,
                onExitToMap = onExitToMap
            )
        }

        // Game Over Dialog (Out of moves)
        if (engineState.isGameOver && !engineState.isGameWon) {
            LevelFailDialog(
                levelNumber = levelConfig.levelNumber,
                profile = profile,
                onWatchAdForMoves = onWatchAdForExtraMoves,
                onRetry = onRetryLevel,
                onExitToMap = onExitToMap
            )
        }

        // Pause Menu Dialog
        if (isPaused) {
            PauseMenuDialog(
                soundEnabled = profile.soundEnabled,
                onToggleSound = onToggleSound,
                onResume = { isPaused = false },
                onRestart = {
                    isPaused = false
                    onRetryLevel()
                },
                onExit = {
                    isPaused = false
                    onExitToMap()
                }
            )
        }
    }
}

@Composable
private fun GameTopHud(
    config: LevelConfig,
    state: MatchEngineState,
    profile: PlayerProfile,
    onPause: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x351E0C3E)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Row: Level title, Score, Pause button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Level ${config.levelNumber}",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = config.worldName,
                        color = NeonCyanSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Score Counter
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "SCORE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${state.currentScore}",
                        color = NeonGoldTertiary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Moves Remaining Pill
                Box(
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = NeonPinkPrimary)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(NeonPinkPrimary, Color(0xFFD500F9))))
                        .border(1.dp, Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "MOVES", color = Color.White.copy(alpha = 0.8f), fontSize = 8.sp, fontWeight = FontWeight.Black)
                        Text(text = "${state.movesLeft}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    }
                }

                // Pause Button
                IconButton(onClick = onPause, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Pause, "Pause", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Star Progress Bar
            val starFraction = (state.currentScore.toFloat() / config.targetScore3Star.toFloat()).coerceIn(0f, 1f)
            GlassProgressBar(progress = starFraction, height = 8.dp)

            Spacer(modifier = Modifier.height(8.dp))

            // Active Goals List Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (goal in state.goals) {
                    GoalChip(goal = goal, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun GoalChip(goal: com.example.data.model.LevelGoal, modifier: Modifier = Modifier) {
    val isDone = goal.isCompleted
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDone) Color(0x3300E676) else Color(0x22000000))
            .border(1.dp, if (isDone) NeonGreenAccent else Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val icon = when (goal.type) {
                GoalType.SCORE -> "⭐"
                GoalType.CLEAR_JELLY -> "🧊"
                GoalType.CLEAR_CHOCOLATE -> "🍫"
                GoalType.COLLECT_INGREDIENTS -> "🍒"
                GoalType.COLLECT_COLOR -> "🍬"
            }
            val actionText = when (goal.type) {
                GoalType.SCORE -> "Score"
                GoalType.CLEAR_JELLY -> "Clear Jelly"
                GoalType.CLEAR_CHOCOLATE -> "Clear Choco"
                GoalType.COLLECT_INGREDIENTS -> "Drop Cherries"
                GoalType.COLLECT_COLOR -> "Collect ${goal.targetColor?.displayName ?: "Candy"}"
            }
            Text(icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            val remaining = maxOf(0, goal.targetAmount - goal.currentAmount)
            Text(
                text = if (isDone) "DONE ✓" else "$actionText: $remaining",
                color = if (isDone) NeonGreenAccent else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun InGameBoosterBar(
    profile: PlayerProfile,
    activeBooster: String?,
    onSelectBooster: (String) -> Unit,
    onWatchAdForExtraMoves: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x3314072B)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Booster 1: Hammer
            BoosterSlotButton(
                icon = "🍭",
                name = "Hammer",
                count = profile.hammerBoosters,
                isActive = activeBooster == "hammer",
                onClick = { onSelectBooster("hammer") }
            )

            // Booster 2: Free Swap
            BoosterSlotButton(
                icon = "🔄",
                name = "Swap",
                count = profile.swapBoosters,
                isActive = activeBooster == "swap",
                onClick = { onSelectBooster("swap") }
            )

            // Booster 3: Color Bomb
            BoosterSlotButton(
                icon = "🌈",
                name = "Bomb",
                count = profile.colorBombBoosters,
                isActive = activeBooster == "bomb",
                onClick = { onSelectBooster("bomb") }
            )

            // Booster 4: Watch Ad for +5 Moves
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF00C9FF), Color(0xFF00E676))))
                    .border(1.dp, Color.White, RoundedCornerShape(12.dp))
                    .clickable { onWatchAdForExtraMoves() }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Videocam, "Ad", tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+5 Moves", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                    Text("FREE AD", color = Color.White.copy(alpha = 0.9f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun BoosterSlotButton(
    icon: String,
    name: String,
    count: Int,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) Color(0x66FF2A85) else Color(0x22FFFFFF))
            .border(
                1.2.dp,
                if (isActive) NeonPinkPrimary else Color.White.copy(alpha = 0.25f),
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text("$count", color = if (count > 0) NeonGoldTertiary else TextMuted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Text(name, color = if (isActive) NeonPinkPrimary else TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LevelWinDialog(
    levelNumber: Int,
    score: Int,
    targetScore1: Int,
    targetScore2: Int,
    targetScore3: Int,
    onNextLevel: () -> Unit,
    onExitToMap: () -> Unit
) {
    val starsEarned = when {
        score >= targetScore3 -> 3
        score >= targetScore2 -> 2
        else -> 1
    }

    Dialog(onDismissRequest = {}) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = Color(0xEE16072E),
            borderColor = NeonGoldTertiary
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🍬 LEVEL COMPLETED! 🍬", color = NeonGoldTertiary, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("Level $levelNumber Clear!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // 3 Stars Reveal Animation
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (s in 1..3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = if (s <= starsEarned) NeonGoldTertiary else Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(if (s == 2) 48.dp else 36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("FINAL SCORE", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("$score", color = Color.White, fontWeight = FontWeight.Black, fontSize = 32.sp)

                Spacer(modifier = Modifier.height(8.dp))

                // Rewards Earned Box
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x3300E676))
                        .border(1.dp, NeonGreenAccent, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("🪙 +${starsEarned * 15 + 25} Coins Earned!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                GlassButton(
                    onClick = onNextLevel,
                    brush = ButtonPinkGlassBrush,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("NEXT LEVEL ➔", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                GlassButton(
                    onClick = onExitToMap,
                    brush = ButtonCyanGlassBrush,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Level Map", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun LevelFailDialog(
    levelNumber: Int,
    profile: PlayerProfile,
    onWatchAdForMoves: () -> Unit,
    onRetry: () -> Unit,
    onExitToMap: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = Color(0xEE22081E),
            borderColor = NeonPinkPrimary
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("💔 OUT OF MOVES!", color = NeonPinkPrimary, fontWeight = FontWeight.Black, fontSize = 22.sp)
                Text("Don't give up on Level $levelNumber!", color = TextSecondary, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Watch Rewarded Ad Continue Option
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0x3300E676),
                    borderColor = NeonGreenAccent
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("CONTINUE PLAYING", color = NeonGreenAccent, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("+5 Extra Moves Instantly!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        GlassButton(
                            onClick = onWatchAdForMoves,
                            brush = ButtonCyanGlassBrush,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Videocam, "Ad", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WATCH AD FOR +5 MOVES", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Retry Button
                GlassButton(
                    onClick = onRetry,
                    brush = ButtonPinkGlassBrush,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, "Retry", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retry Level (-1 ❤️)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Exit Button
                GlassButton(
                    onClick = onExitToMap,
                    brush = Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x11FFFFFF))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Exit to Map", color = TextSecondary, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun PauseMenuDialog(
    soundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            backgroundColor = Color(0xEE1A0835)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("GAME PAUSED", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Sound Toggle Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable { onToggleSound() }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute, "Sound", tint = Color.White)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Sound & Chimes", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                    GlassBadge(text = if (soundEnabled) "ON" else "OFF", color = if (soundEnabled) NeonGreenAccent else TextMuted)
                }

                Spacer(modifier = Modifier.height(14.dp))

                GlassButton(
                    onClick = onResume,
                    brush = ButtonPinkGlassBrush,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("RESUME", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                GlassButton(
                    onClick = onRestart,
                    brush = ButtonCyanGlassBrush,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restart Level", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                GlassButton(
                    onClick = onExit,
                    brush = Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x11FFFFFF))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Quit to Map", color = TextSecondary, fontSize = 13.sp)
                }
            }
        }
    }
}

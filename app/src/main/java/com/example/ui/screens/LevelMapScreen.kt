package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ads.RewardReason
import com.example.data.local.LevelProgress
import com.example.data.local.PlayerProfile
import com.example.data.model.GoalType
import com.example.data.model.LevelConfig
import com.example.engine.LevelGenerator
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
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
fun LevelMapScreen(
    profile: PlayerProfile,
    levelProgressList: List<LevelProgress>,
    onSelectLevel: (Int, Boolean) -> Unit,
    onOpenShop: () -> Unit,
    onOpenDailySpin: () -> Unit,
    onOpenMonetizationStats: () -> Unit,
    onWatchAdForLives: () -> Unit,
    onToggleSound: () -> Unit,
    modifier: Modifier = Modifier
) {
    val highestUnlockedLevel = levelProgressList.filter { it.isUnlocked }.maxOfOrNull { it.levelId } ?: 1
    val listState = rememberLazyListState()

    // Auto scroll near highest unlocked level on start
    LaunchedEffect(highestUnlockedLevel) {
        val targetIndex = (350 - highestUnlockedLevel).coerceIn(0, 349)
        listState.scrollToItem(targetIndex)
    }

    var previewLevelNumber by remember { mutableStateOf<Int?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        // Level Road Map Scrollable
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 110.dp, bottom = 90.dp),
            reverseLayout = false
        ) {
            // Display levels 150 down to 1 (climbing up the mountain of candy)
            itemsIndexed((350 downTo 1).toList()) { index, levelNum ->
                val progress = levelProgressList.find { it.levelId == levelNum }
                val isUnlocked = progress?.isUnlocked == true || levelNum == 1
                val stars = progress?.stars ?: 0
                val isCurrent = levelNum == highestUnlockedLevel

                // Calculate winding road horizontal offset (-1f to 1f)
                val curveOffset = Math.sin(levelNum * 0.45).toFloat() * 110f

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // World Biome Header Banner if at world boundary
                    if (levelNum % 50 == 0) {
                        val (worldName, worldIdx) = LevelGenerator.getWorldInfoForLevel(levelNum)
                        BiomeHeaderBanner(worldName = worldName, worldIndex = worldIdx)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Level Node
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Sinuous glowing road connector line
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val centerX = w * 0.5f + curveOffset
                            val nextCurve = Math.sin((levelNum - 1) * 0.45).toFloat() * 110f
                            val nextX = w * 0.5f + nextCurve

                            drawLine(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        if (isUnlocked) NeonCyanSecondary.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.2f),
                                        if (isUnlocked) NeonPinkPrimary.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.1f)
                                    )
                                ),
                                start = Offset(centerX, h * 0.5f),
                                end = Offset(nextX, h * 1.5f),
                                strokeWidth = 8f
                            )
                        }

                        // Interactive Level Pin
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(
                                    start = if (curveOffset > 0) curveOffset.dp else 0.dp,
                                    end = if (curveOffset < 0) (-curveOffset).dp else 0.dp
                                )
                        ) {
                            LevelNodeItem(
                                levelNumber = levelNum,
                                stars = stars,
                                isUnlocked = isUnlocked,
                                isCurrent = isCurrent,
                                onClick = {
                                    if (isUnlocked) {
                                        previewLevelNumber = levelNum
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Top Glass HUD Bar
        TopMapHudBar(
            profile = profile,
            onOpenShop = onOpenShop,
            onOpenDailySpin = onOpenDailySpin,
            onOpenMonetizationStats = onOpenMonetizationStats,
            onWatchAdForLives = onWatchAdForLives,
            onToggleSound = onToggleSound,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    // Level Preview & VIP Start Dialog
    previewLevelNumber?.let { lvl ->
        val config = remember(lvl) { LevelGenerator.getLevel(lvl) }
        LevelPreviewDialog(
            config = config,
            profile = profile,
            onPlay = { useVipColorBomb ->
                previewLevelNumber = null
                onSelectLevel(lvl, useVipColorBomb)
            },
            onDismiss = { previewLevelNumber = null },
            onWatchAdForLives = onWatchAdForLives
        )
    }
}

@Composable
private fun TopMapHudBar(
    profile: PlayerProfile,
    onOpenShop: () -> Unit,
    onOpenDailySpin: () -> Unit,
    onOpenMonetizationStats: () -> Unit,
    onWatchAdForLives: () -> Unit,
    onToggleSound: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        backgroundColor = Color(0xDD190933),
        elevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lives with + refill button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .border(1.dp, NeonPinkPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable { onWatchAdForLives() }
            ) {
                Text("❤️", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${profile.lives}/${profile.maxLives}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(NeonGreenAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, "Refill", tint = Color.Black, modifier = Modifier.size(14.dp))
                }
            }

            // Coins
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .border(1.dp, NeonGoldTertiary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable { onOpenShop() }
            ) {
                Text("🪙", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${profile.coins}",
                    color = NeonGoldTertiary,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }

            // Quick Actions: Spin, Shop, Monetization, Sound
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Daily Spin
                IconButton(onClick = onOpenDailySpin, modifier = Modifier.size(36.dp)) {
                    Text("🎡", fontSize = 20.sp)
                }

                // Shop
                IconButton(onClick = onOpenShop, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ShoppingBag, "Shop", tint = NeonCyanSecondary, modifier = Modifier.size(20.dp))
                }

                // Monetization Stats
                IconButton(onClick = onOpenMonetizationStats, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.BarChart, "Ad Stats", tint = NeonGreenAccent, modifier = Modifier.size(20.dp))
                }

                // Sound Toggle
                IconButton(onClick = onToggleSound, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (profile.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Sound",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelNodeItem(
    levelNumber: Int,
    stars: Int,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pinPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(enabled = isUnlocked, onClick = onClick)
    ) {
        if (isCurrent) {
            Text("👑", fontSize = 16.sp, modifier = Modifier.scale(pulseScale))
        }

        Box(
            modifier = Modifier
                .size(56.dp)
                .scale(if (isCurrent) pulseScale else 1f)
                .shadow(
                    elevation = if (isUnlocked) 12.dp else 2.dp,
                    shape = CircleShape,
                    spotColor = if (isCurrent) NeonGoldTertiary else if (isUnlocked) NeonPinkPrimary else Color.Transparent
                )
                .clip(CircleShape)
                .background(
                    if (isUnlocked) {
                        Brush.linearGradient(
                            if (isCurrent) listOf(Color(0xFFFFD000), Color(0xFFFF6D00))
                            else listOf(NeonPinkPrimary, Color(0xFF7928CA))
                        )
                    } else {
                        Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x1AFFFFFF)))
                    }
                )
                .border(
                    width = 2.5.dp,
                    color = if (isCurrent) Color.White else if (isUnlocked) Color(0xCCFFFFFF) else Color(0x33FFFFFF),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                Text(
                    text = "$levelNumber",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Stars row under node
        if (isUnlocked && stars > 0) {
            Row(
                modifier = Modifier.padding(top = 2.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for (s in 1..3) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star $s",
                        tint = if (s <= stars) NeonGoldTertiary else Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BiomeHeaderBanner(worldName: String, worldIndex: Int) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        backgroundColor = Color(0x4400E5FF),
        borderColor = NeonCyanSecondary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "WORLD $worldIndex",
                    color = NeonGoldTertiary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = worldName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }
            GlassBadge(text = "Levels ${(worldIndex - 1) * 20 + 1}-${worldIndex * 20}", color = NeonCyanSecondary)
        }
    }
}

@Composable
private fun LevelPreviewDialog(
    config: LevelConfig,
    profile: PlayerProfile,
    onPlay: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onWatchAdForLives: () -> Unit
) {
    var useVipColorBomb by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = Color(0xEE16072C)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Level ${config.levelNumber}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
                        Text(config.worldName, color = NeonCyanSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Goals Box
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0x22FFFFFF)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("TARGET GOALS:", color = NeonGoldTertiary, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        for (goal in config.goals) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Text("✦", color = NeonCyanSecondary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                val label = when (goal.type) {
                                    GoalType.SCORE -> "Reach Score: ${goal.targetAmount}"
                                    GoalType.CLEAR_JELLY -> "Clear ${goal.targetAmount} Frosted Jelly"
                                    GoalType.CLEAR_CHOCOLATE -> "Break ${goal.targetAmount} Chocolates"
                                    GoalType.COLLECT_INGREDIENTS -> "Bring ${goal.targetAmount} Cherries to Bottom"
                                    GoalType.COLLECT_COLOR -> "Collect ${goal.targetAmount} ${goal.targetColor?.displayName ?: ""} Candies"
                                }
                                Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Moves Allowed: ${config.maxMoves}", color = TextMuted, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // VIP Rewarded Interstitial Option (Pre-game Boost)
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { useVipColorBomb = !useVipColorBomb },
                    backgroundColor = if (useVipColorBomb) Color(0x44FF2A85) else Color(0x18FFFFFF),
                    borderColor = if (useVipColorBomb) NeonPinkPrimary else Color.White.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌈", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Start with Color Bomb", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("VIP Rewarded Boost", color = NeonGoldTertiary, fontSize = 10.sp)
                            }
                        }
                        GlassBadge(
                            text = if (useVipColorBomb) "SELECTED ✓" else "TAP TO ADD",
                            color = if (useVipColorBomb) NeonGreenAccent else NeonCyanSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Play Button
                if (profile.lives > 0) {
                    GlassButton(
                        onClick = { onPlay(useVipColorBomb) },
                        brush = ButtonPinkGlassBrush,
                        modifier = Modifier.fillMaxWidth(),
                        padding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PLAY (-1 ❤️)", color = Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp)
                    }
                } else {
                    GlassButton(
                        onClick = {
                            onDismiss()
                            onWatchAdForLives()
                        },
                        brush = ButtonCyanGlassBrush,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Videocam, "Ad", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Out of Lives! Watch Ad for +5 ❤️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

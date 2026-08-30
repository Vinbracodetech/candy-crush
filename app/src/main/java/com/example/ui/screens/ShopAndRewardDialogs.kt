package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ads.RewardReason
import com.example.data.local.PlayerProfile
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
import kotlin.random.Random

@Composable
fun BoosterShopDialog(
    profile: PlayerProfile,
    onBuyBooster: (type: String, cost: Int) -> Unit,
    onWatchAdForReward: (RewardReason) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            backgroundColor = Color(0xEE1A0A33)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🍭 Candy Shop & Free Boosts",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White)
                    }
                }

                // Balance HUD
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${profile.coins} Coins", color = NeonGoldTertiary, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("❤️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${profile.lives}/${profile.maxLives} Lives", color = NeonPinkPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Watch Ad for Instant Full Lives
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0x33FF2A85),
                    borderColor = NeonPinkPrimary
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💖", fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Refill All 5 Lives", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Watch 1 Rewarded Ad", color = NeonCyanSecondary, fontSize = 11.sp)
                            }
                        }
                        GlassButton(
                            onClick = {
                                onDismiss()
                                onWatchAdForReward(RewardReason.EXTRA_LIVES)
                            },
                            brush = ButtonPinkGlassBrush,
                            padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Videocam, "Ad", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("FREE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Booster Item 1: Lollipop Hammer
                ShopItemRow(
                    icon = "🍭",
                    title = "Lollipop Hammer",
                    ownedCount = profile.hammerBoosters,
                    coinCost = 80,
                    canAfford = profile.coins >= 80,
                    onBuy = { onBuyBooster("hammer", 80) },
                    onWatchAd = {
                        onDismiss()
                        onWatchAdForReward(RewardReason.FREE_HAMMER)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Booster Item 2: Free Hand Swap
                ShopItemRow(
                    icon = "🔄",
                    title = "Free Hand Swap",
                    ownedCount = profile.swapBoosters,
                    coinCost = 100,
                    canAfford = profile.coins >= 100,
                    onBuy = { onBuyBooster("swap", 100) },
                    onWatchAd = {
                        onDismiss()
                        onWatchAdForReward(RewardReason.FREE_SWAP)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Booster Item 3: Color Bomb Start
                ShopItemRow(
                    icon = "🌈",
                    title = "Rainbow Color Bomb",
                    ownedCount = profile.colorBombBoosters,
                    coinCost = 120,
                    canAfford = profile.coins >= 120,
                    onBuy = { onBuyBooster("bomb", 120) },
                    onWatchAd = {
                        onDismiss()
                        onWatchAdForReward(RewardReason.COLOR_BOMB_START)
                    }
                )
            }
        }
    }
}

@Composable
private fun ShopItemRow(
    icon: String,
    title: String,
    ownedCount: Int,
    coinCost: Int,
    canAfford: Boolean,
    onBuy: () -> Unit,
    onWatchAd: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x22FFFFFF),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Owned: $ownedCount", color = TextMuted, fontSize = 11.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Free Ad Button
                GlassButton(
                    onClick = onWatchAd,
                    brush = ButtonCyanGlassBrush,
                    padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Videocam, "Ad", tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Free", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Buy with Coins Button
                GlassButton(
                    onClick = onBuy,
                    enabled = canAfford,
                    brush = ButtonGoldGlassBrush,
                    padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("🪙 $coinCost", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DailyLuckyWheelDialog(
    onRewardWon: (rewardText: String, coins: Int, hammer: Int, swap: Int, bomb: Int) -> Unit,
    onWatchAdForExtraSpin: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        var isSpinning by remember { mutableStateOf(false) }
        var targetRotation by remember { mutableFloatStateOf(0f) }
        var wonPrize by remember { mutableStateOf<String?>(null) }

        val animatedRotation by animateFloatAsState(
            targetValue = targetRotation,
            animationSpec = tween(3500, easing = FastOutSlowInEasing),
            finishedListener = {
                isSpinning = false
                // Determine prize based on angle
                val normalized = (360f - (targetRotation % 360f)) % 360f
                when {
                    normalized < 60 -> {
                        wonPrize = "+100 Coins! 🪙"
                        onRewardWon("100 Coins", 100, 0, 0, 0)
                    }
                    normalized < 120 -> {
                        wonPrize = "1 Lollipop Hammer! 🍭"
                        onRewardWon("Lollipop Hammer", 0, 1, 0, 0)
                    }
                    normalized < 180 -> {
                        wonPrize = "+250 Jackpot Coins! 💰"
                        onRewardWon("250 Coins", 250, 0, 0, 0)
                    }
                    normalized < 240 -> {
                        wonPrize = "1 Free Hand Swap! 🔄"
                        onRewardWon("Free Hand Swap", 0, 0, 1, 0)
                    }
                    normalized < 300 -> {
                        wonPrize = "1 Rainbow Color Bomb! 🌈"
                        onRewardWon("Rainbow Color Bomb", 0, 0, 0, 1)
                    }
                    else -> {
                        wonPrize = "+500 Mega Gold! 🏆"
                        onRewardWon("500 Coins", 500, 0, 0, 0)
                    }
                }
            },
            label = "wheelRotation"
        )

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = Color(0xEE16072E)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎡 Daily Lucky Spin", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Wheel Container with Needle
                Box(
                    modifier = Modifier.size(230.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating Wheel
                    Box(
                        modifier = Modifier
                            .size(210.dp)
                            .rotate(animatedRotation)
                            .shadow(16.dp, CircleShape, spotColor = NeonPinkPrimary)
                            .clip(CircleShape)
                            .border(3.dp, Color.White, CircleShape)
                    ) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val w = size.width
                            val h = size.height
                            val colors = listOf(
                                Color(0xFFFF2A85),
                                Color(0xFF00C9FF),
                                Color(0xFFFFD000),
                                Color(0xFF00E676),
                                Color(0xFF9C27B0),
                                Color(0xFFFF7043)
                            )
                            for (i in 0 until 6) {
                                drawArc(
                                    color = colors[i],
                                    startAngle = i * 60f,
                                    sweepAngle = 60f,
                                    useCenter = true
                                )
                            }
                            drawCircle(color = Color.White.copy(alpha = 0.3f), radius = w * 0.45f, style = Stroke(2f))
                        }
                    }

                    // Center Pin
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Color.White, NeonGoldTertiary)))
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("★", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }

                    // Top Pointer Needle
                    Text(
                        "▼",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (wonPrize != null) {
                    Text(
                        text = "YOU WON: $wonPrize",
                        color = NeonGreenAccent,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Spin Action Buttons
                if (wonPrize == null) {
                    GlassButton(
                        onClick = {
                            if (!isSpinning) {
                                isSpinning = true
                                targetRotation = targetRotation + 1440f + Random.nextInt(360).toFloat()
                            }
                        },
                        brush = ButtonPinkGlassBrush,
                        enabled = !isSpinning,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isSpinning) "SPINNING..." else "SPIN FOR FREE!", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GlassButton(
                            onClick = {
                                onDismiss()
                                onWatchAdForExtraSpin()
                            },
                            brush = ButtonCyanGlassBrush,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Videocam, "Ad", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Extra Spin (Ad)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        GlassButton(
                            onClick = onDismiss,
                            brush = ButtonGoldGlassBrush,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Collect & Play", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

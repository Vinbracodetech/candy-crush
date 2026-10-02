package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    cooldownSeconds: Long = 0L,
    coinsCooldownSeconds: Long = 0L,
    onBuyBooster: (type: String, cost: Int) -> Unit,
    onBuyLives: (cost: Int) -> Unit = {},
    onWatchAdForReward: (RewardReason) -> Unit,
    onDismiss: () -> Unit
) {
    val isCooldown = cooldownSeconds > 0L
    val cooldownText = if (isCooldown) {
        val m = cooldownSeconds / 60
        val s = cooldownSeconds % 60
        "${m}m ${s}s"
    } else ""

    val isCoinsCooldown = coinsCooldownSeconds > 0L
    val coinsCooldownText = if (isCoinsCooldown) {
        val m = coinsCooldownSeconds / 60
        val s = coinsCooldownSeconds % 60
        "${m}m ${s}s"
    } else ""

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            backgroundColor = Color(0xF2160C26),
            borderColor = Color(0x40FFFFFF),
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Candy Boutique",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Boosters & Rewards",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                    ) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Balance HUD - Soft subtle dark slate container
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x550B0418))
                        .border(1.dp, Color(0x25FFFFFF), RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙", fontSize = 17.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${profile.coins} Coins",
                            color = Color(0xFFFFD54F),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("❤️", fontSize = 17.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${profile.lives}/${profile.maxLives} Lives",
                            color = Color(0xFFFF80AB),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Get Free Coins (+100 Gold Coins) - Soft warm honey glass
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0x1EFFE082),
                    borderColor = Color(0x55FFCA28),
                    borderWidth = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🪙", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("+100 Gold Coins", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text(
                                    if (isCoinsCooldown) "Available in $coinsCooldownText" else "Free bonus video",
                                    color = Color(0xFFFFE082),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        GlassButton(
                            onClick = {
                                onDismiss()
                                onWatchAdForReward(RewardReason.COINS_PACK)
                            },
                            enabled = !isCoinsCooldown,
                            brush = if (!isCoinsCooldown) {
                                Brush.horizontalGradient(listOf(Color(0xFFE6A728), Color(0xFFC7841B)))
                            } else {
                                Brush.linearGradient(listOf(Color(0x22FFFFFF), Color(0x11FFFFFF)))
                            },
                            padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Icon(Icons.Default.Videocam, "Ad", tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (isCoinsCooldown) coinsCooldownText else "+100 🪙",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Refill All 5 Lives - Soft blush rose glass
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0x18FF80AB),
                    borderColor = Color(0x45FF80AB),
                    borderWidth = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💖", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Full Lives Refill", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("Restore 5/5 lives instantly", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Watch Ad
                            GlassButton(
                                onClick = {
                                    onDismiss()
                                    onWatchAdForReward(RewardReason.EXTRA_LIVES)
                                },
                                enabled = !isCooldown,
                                brush = if (!isCooldown) {
                                    Brush.horizontalGradient(listOf(Color(0xFF8E44AD), Color(0xFF6C3483)))
                                } else {
                                    Brush.linearGradient(listOf(Color(0x22FFFFFF), Color(0x11FFFFFF)))
                                },
                                modifier = Modifier.weight(1f),
                                padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 7.dp)
                            ) {
                                Icon(Icons.Default.Videocam, "Ad", tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isCooldown) cooldownText else "Free Ad", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            // Buy with Coins (200 coins)
                            GlassButton(
                                onClick = {
                                    onDismiss()
                                    onBuyLives(200)
                                },
                                enabled = profile.coins >= 200,
                                brush = Brush.horizontalGradient(listOf(Color(0xFF7A6432), Color(0xFF665226))),
                                modifier = Modifier.weight(1f),
                                padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 7.dp)
                            ) {
                                Text("🪙 200 Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section Label
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "POWER-UP BOOSTERS",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Booster Item 1: Lollipop Hammer
                ShopItemRow(
                    icon = "🍭",
                    title = "Lollipop Hammer",
                    description = "Smash any single candy",
                    ownedCount = profile.hammerBoosters,
                    coinCost = 80,
                    canAfford = profile.coins >= 80,
                    isCooldown = isCooldown,
                    cooldownText = cooldownText,
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
                    title = "Hand Swap",
                    description = "Swap 2 tiles without moves",
                    ownedCount = profile.swapBoosters,
                    coinCost = 100,
                    canAfford = profile.coins >= 100,
                    isCooldown = isCooldown,
                    cooldownText = cooldownText,
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
                    title = "Color Bomb",
                    description = "Clear all of chosen candy",
                    ownedCount = profile.colorBombBoosters,
                    coinCost = 120,
                    canAfford = profile.coins >= 120,
                    isCooldown = isCooldown,
                    cooldownText = cooldownText,
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
    description: String = "",
    ownedCount: Int,
    coinCost: Int,
    canAfford: Boolean,
    isCooldown: Boolean = false,
    cooldownText: String = "",
    onBuy: () -> Unit,
    onWatchAd: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x18FFFFFF),
        borderColor = Color(0x28FFFFFF),
        borderWidth = 1.dp,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x22FFFFFF))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    if (description.isNotEmpty()) {
                        Text(description, color = TextMuted, fontSize = 10.sp)
                    }
                    Text("Owned: $ownedCount", color = Color(0xFFC7BCE6), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Free Ad Button - Muted subtle lavender blue
                GlassButton(
                    onClick = onWatchAd,
                    enabled = !isCooldown,
                    brush = if (!isCooldown) {
                        Brush.horizontalGradient(listOf(Color(0xFF3A506B), Color(0xFF2C3E50)))
                    } else {
                        Brush.linearGradient(listOf(Color(0x22FFFFFF), Color(0x11FFFFFF)))
                    },
                    padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Videocam, "Ad", tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        if (isCooldown) cooldownText else "Free",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Buy with Coins Button - Soft honey amber
                GlassButton(
                    onClick = onBuy,
                    enabled = canAfford,
                    brush = if (canAfford) {
                        Brush.horizontalGradient(listOf(Color(0xFFB8860B), Color(0xFF8F6307)))
                    } else {
                        Brush.linearGradient(listOf(Color(0x22FFFFFF), Color(0x11FFFFFF)))
                    },
                    padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "🪙 $coinCost",
                        color = if (canAfford) Color.White else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
                                Color(0xFF9B51E0), // Soft Royal Amethyst
                                Color(0xFF2D9CDB), // Soft Cerulean
                                Color(0xFFD4AC0D), // Warm Amber
                                Color(0xFF27AE60), // Emerald Sage
                                Color(0xFFD9455F), // Rose Coral
                                Color(0xFF6C5CE7)  // Periwinkle
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

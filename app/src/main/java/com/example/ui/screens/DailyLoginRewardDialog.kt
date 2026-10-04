package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.PlayerProfile
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.theme.ButtonGoldGlassBrush
import com.example.ui.theme.ButtonPinkGlassBrush
import com.example.ui.theme.NeonCyanSecondary
import com.example.ui.theme.NeonGoldTertiary
import com.example.ui.theme.NeonGreenAccent
import com.example.ui.theme.NeonPinkPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

data class DailyLoginReward(
    val dayNumber: Int,
    val title: String,
    val icon: String,
    val description: String,
    val coins: Int = 0,
    val hammer: Int = 0,
    val swap: Int = 0,
    val bomb: Int = 0,
    val isJackpot: Boolean = false
)

object DailyLoginRewardConfig {
    val weeklyRewards = listOf(
        DailyLoginReward(
            dayNumber = 1,
            title = "Day 1",
            icon = "🔨",
            description = "+1 Lollipop Hammer",
            hammer = 1,
            coins = 50
        ),
        DailyLoginReward(
            dayNumber = 2,
            title = "Day 2",
            icon = "🔄",
            description = "+1 Free Hand Swap",
            swap = 1,
            coins = 75
        ),
        DailyLoginReward(
            dayNumber = 3,
            title = "Day 3",
            icon = "🌈",
            description = "+1 Rainbow Color Bomb",
            bomb = 1,
            coins = 100
        ),
        DailyLoginReward(
            dayNumber = 4,
            title = "Day 4",
            icon = "🔨",
            description = "+1 Hammer & +1 Swap",
            hammer = 1,
            swap = 1,
            coins = 100
        ),
        DailyLoginReward(
            dayNumber = 5,
            title = "Day 5",
            icon = "🌈",
            description = "+1 Bomb & +1 Hammer",
            bomb = 1,
            hammer = 1,
            coins = 150
        ),
        DailyLoginReward(
            dayNumber = 6,
            title = "Day 6",
            icon = "🎁",
            description = "+1 All 3 Boosters Pack",
            hammer = 1,
            swap = 1,
            bomb = 1,
            coins = 200
        ),
        DailyLoginReward(
            dayNumber = 7,
            title = "Day 7",
            icon = "👑",
            description = "+2 All Boosters & +350 Coins!",
            hammer = 2,
            swap = 2,
            bomb = 2,
            coins = 350,
            isJackpot = true
        )
    )
}

@Composable
fun DailyLoginRewardDialog(
    currentStreak: Int,
    isClaimableToday: Boolean,
    onClaimReward: (reward: DailyLoginReward) -> Unit,
    onDismiss: () -> Unit
) {
    // Current day in 7-day loop (1 to 7)
    val activeDay = if (currentStreak <= 0) 1 else ((currentStreak - 1) % 7) + 1
    val targetReward = DailyLoginRewardConfig.weeklyRewards.find { it.dayNumber == activeDay }
        ?: DailyLoginRewardConfig.weeklyRewards[0]

    var hasClaimedLocally by remember { mutableStateOf(!isClaimableToday) }
    var claimedRewardInfo by remember { mutableStateOf<DailyLoginReward?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            backgroundColor = Color(0xF2160A2A),
            borderColor = NeonGoldTertiary.copy(alpha = 0.9f),
            elevation = 22.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎁", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "DAILY LOGIN REWARDS",
                                color = NeonGoldTertiary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                "Streak: $currentStreak Days in a Row 🔥",
                                color = NeonCyanSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isClaimableToday && !hasClaimedLocally) {
                        "Welcome back! Tap below to claim today's power-ups and boosters."
                    } else {
                        "You've claimed today's booster! Come back tomorrow for Day ${if (activeDay == 7) 1 else activeDay + 1}."
                    },
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 7-Day Calendar Grid
                // Row 1: Days 1 to 4
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (reward in DailyLoginRewardConfig.weeklyRewards.take(4)) {
                        DailyDayCard(
                            reward = reward,
                            currentDayNumber = activeDay,
                            isClaimableToday = isClaimableToday && !hasClaimedLocally,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2: Days 5 to 7 (Day 7 is large Jackpot Card)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DailyDayCard(
                        reward = DailyLoginRewardConfig.weeklyRewards[4],
                        currentDayNumber = activeDay,
                        isClaimableToday = isClaimableToday && !hasClaimedLocally,
                        modifier = Modifier.weight(1f)
                    )
                    DailyDayCard(
                        reward = DailyLoginRewardConfig.weeklyRewards[5],
                        currentDayNumber = activeDay,
                        isClaimableToday = isClaimableToday && !hasClaimedLocally,
                        modifier = Modifier.weight(1f)
                    )
                    DailyDayCard(
                        reward = DailyLoginRewardConfig.weeklyRewards[6],
                        currentDayNumber = activeDay,
                        isClaimableToday = isClaimableToday && !hasClaimedLocally,
                        modifier = Modifier.weight(2f) // Double width for Day 7 jackpot
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Highlighted reward details card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x35000000))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(targetReward.icon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Day $activeDay: ${targetReward.description}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "+${targetReward.coins} Bonus Coins included! 🪙",
                                    color = NeonGoldTertiary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        if (!isClaimableToday || hasClaimedLocally) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonGreenAccent.copy(alpha = 0.25f))
                                    .border(1.dp, NeonGreenAccent, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("CLAIMED", color = NeonGreenAccent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Claim Button
                if (isClaimableToday && !hasClaimedLocally) {
                    GlassButton(
                        onClick = {
                            hasClaimedLocally = true
                            claimedRewardInfo = targetReward
                            onClaimReward(targetReward)
                        },
                        brush = ButtonGoldGlassBrush,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CLAIM DAY $activeDay REWARD! 🎁", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }
                } else {
                    GlassButton(
                        onClick = onDismiss,
                        brush = ButtonPinkGlassBrush,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("GREAT! CONTINUE PLAYING", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyDayCard(
    reward: DailyLoginReward,
    currentDayNumber: Int,
    isClaimableToday: Boolean,
    modifier: Modifier = Modifier
) {
    val isPastClaimed = reward.dayNumber < currentDayNumber
    val isToday = reward.dayNumber == currentDayNumber
    val isFuture = reward.dayNumber > currentDayNumber

    val cardBg = when {
        isToday -> if (reward.isJackpot) Color(0x66FFD700) else Color(0x55FF2A85)
        isPastClaimed -> Color(0x3500E676)
        else -> Color(0x22FFFFFF)
    }

    val cardBorder = when {
        isToday -> if (reward.isJackpot) NeonGoldTertiary else NeonPinkPrimary
        isPastClaimed -> NeonGreenAccent.copy(alpha = 0.6f)
        else -> Color.White.copy(alpha = 0.15f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(if (isToday) 1.5.dp else 1.dp, cardBorder, RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Day label
            Text(
                text = reward.title,
                color = if (isToday) Color.White else TextMuted,
                fontWeight = if (isToday) FontWeight.Black else FontWeight.Bold,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Icon with checkmark overlay if claimed
            Box(contentAlignment = Alignment.Center) {
                Text(reward.icon, fontSize = if (reward.isJackpot) 24.sp else 20.sp)
                if (isPastClaimed) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(NeonGreenAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, "Claimed", tint = Color.Black, modifier = Modifier.size(13.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Booster quantity description
            val boosterBrief = when {
                reward.isJackpot -> "+2 ALL"
                reward.bomb > 0 && reward.hammer > 0 -> "+1💣+1🔨"
                reward.hammer > 0 && reward.swap > 0 -> "+1🔨+1🔄"
                reward.hammer > 0 -> "+1 🔨"
                reward.swap > 0 -> "+1 🔄"
                reward.bomb > 0 -> "+1 🌈"
                else -> "+${reward.coins}🪙"
            }

            Text(
                text = boosterBrief,
                color = if (isToday) NeonGoldTertiary else Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Black,
                fontSize = 9.sp
            )
        }
    }
}

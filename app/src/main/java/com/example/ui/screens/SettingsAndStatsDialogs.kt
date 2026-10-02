package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.LevelProgress
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
import com.example.ui.theme.TextSecondary

@Composable
fun HowToPlayDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            backgroundColor = Color(0xEE16072E),
            borderColor = NeonCyanSecondary
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📖 HOW TO PLAY",
                        color = NeonCyanSecondary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                HowToPlayCard(
                    icon = "👆",
                    title = "Swap & Match 3",
                    desc = "Tap two adjacent candies or swipe across them to swap and match 3 or more of the same color."
                )

                Spacer(modifier = Modifier.height(10.dp))

                HowToPlayCard(
                    icon = "⚡",
                    title = "Create Special Candies",
                    desc = "• Match 4 in a line: Striped Candy (clears entire row or column).\n• Match 5 in T or L shape: Wrapped Bomb (explodes 3x3 area twice).\n• Match 5 in a row: Rainbow Color Bomb (clears all of chosen color)!"
                )

                Spacer(modifier = Modifier.height(10.dp))

                HowToPlayCard(
                    icon = "💥",
                    title = "Super Combos",
                    desc = "Swap two special candies together for devastating cascades! Color Bomb + Striped turns whole boards into rockets!"
                )

                Spacer(modifier = Modifier.height(10.dp))

                HowToPlayCard(
                    icon = "🎯",
                    title = "Complete Level Goals",
                    desc = "Collect designated fruits (Tangerines, Lemons, Strawberries), clear icy frosting, smash chocolate blocks, and bring cherries down before moves run out!"
                )

                Spacer(modifier = Modifier.height(16.dp))

                GlassButton(
                    onClick = onDismiss,
                    brush = ButtonCyanGlassBrush,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("GOT IT, LET'S PLAY! 🍭", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun HowToPlayCard(icon: String, title: String, desc: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x33000000))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = NeonGoldTertiary, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(3.dp))
                Text(desc, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
    }
}

private data class AchievementItem(
    val id: String,
    val icon: String,
    val title: String,
    val description: String,
    val target: Int,
    val current: Int,
    val rewardCoins: Int
)

@Composable
fun AchievementsDialog(
    profile: PlayerProfile,
    levelProgressList: List<LevelProgress>,
    onDismiss: () -> Unit
) {
    val totalStars = levelProgressList.sumOf { it.stars }
    val maxLevel = levelProgressList.maxOfOrNull { it.levelId } ?: 1
    val completedLevels = levelProgressList.count { it.stars > 0 }

    val achievements = listOf(
        AchievementItem(
            id = "first_steps",
            icon = "🍬",
            title = "Sugar Novice",
            description = "Complete your very first level",
            target = 1,
            current = completedLevels,
            rewardCoins = 50
        ),
        AchievementItem(
            id = "star_collector",
            icon = "⭐",
            title = "Star Explorer",
            description = "Earn 30 Stars across the worlds",
            target = 30,
            current = totalStars,
            rewardCoins = 150
        ),
        AchievementItem(
            id = "star_master",
            icon = "🌟",
            title = "Constellation Master",
            description = "Earn 100 Stars across levels",
            target = 100,
            current = totalStars,
            rewardCoins = 400
        ),
        AchievementItem(
            id = "world_traveler",
            icon = "🗺️",
            title = "World Adventurer",
            description = "Reach Level 15 on the map",
            target = 15,
            current = maxLevel,
            rewardCoins = 200
        ),
        AchievementItem(
            id = "grand_master",
            icon = "👑",
            title = "Candy Royalty",
            description = "Reach Level 50",
            target = 50,
            current = maxLevel,
            rewardCoins = 500
        ),
        AchievementItem(
            id = "coin_hoarder",
            icon = "🪙",
            title = "Gold Hoarder",
            description = "Accumulate 300 total coins in your bank",
            target = 300,
            current = profile.coins,
            rewardCoins = 100
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            backgroundColor = Color(0xEE16072E),
            borderColor = NeonGoldTertiary
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏆", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "ACHIEVEMENTS",
                            color = NeonGoldTertiary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White)
                    }
                }

                Text(
                    "Completed: ${achievements.count { it.current >= it.target }} / ${achievements.size}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                for (ach in achievements) {
                    val isUnlocked = ach.current >= ach.target
                    val fraction = (ach.current.toFloat() / ach.target.toFloat()).coerceIn(0f, 1f)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isUnlocked) Color(0x2200E676) else Color(0x22000000))
                            .border(
                                1.dp,
                                if (isUnlocked) NeonGreenAccent else Color.White.copy(alpha = 0.15f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(ach.icon, fontSize = 26.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        ach.title,
                                        color = if (isUnlocked) NeonGoldTertiary else Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        ach.description,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .width(90.dp)
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(Color.White.copy(alpha = 0.15f))
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(fraction)
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(if (isUnlocked) NeonGreenAccent else NeonCyanSecondary)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "${minOf(ach.current, ach.target)}/${ach.target}",
                                            color = TextMuted,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Reward Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isUnlocked) NeonGreenAccent else Color(0x33FFD700))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    if (isUnlocked) "UNLOCKED ✓" else "+${ach.rewardCoins} 🪙",
                                    color = if (isUnlocked) Color.Black else Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlassButton(
                    onClick = onDismiss,
                    brush = ButtonGoldGlassBrush,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CLOSE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun GameSettingsDialog(
    profile: PlayerProfile,
    onToggleSound: () -> Unit,
    onOpenHowToPlay: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenShop: () -> Unit,
    onDismiss: () -> Unit
) {
    var hapticEnabled by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            backgroundColor = Color(0xEE16072E),
            borderColor = NeonPinkPrimary
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚙️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "GAME SETTINGS",
                            color = NeonPinkPrimary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sound Toggle Row
                SettingItemCard(
                    icon = if (profile.soundEnabled) "🔊" else "🔇",
                    title = "Sound Effects & Audio",
                    subtitle = if (profile.soundEnabled) "Enabled (Chimes & Combos)" else "Muted"
                ) {
                    Switch(
                        checked = profile.soundEnabled,
                        onCheckedChange = { onToggleSound() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonPinkPrimary,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Haptic Feedback Toggle Row
                SettingItemCard(
                    icon = "📳",
                    title = "Vibration & Haptics",
                    subtitle = if (hapticEnabled) "Active on Combos & Bombs" else "Disabled"
                ) {
                    Switch(
                        checked = hapticEnabled,
                        onCheckedChange = { hapticEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonCyanSecondary,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Navigation Shortcut: Sweet Shop
                GlassButton(
                    onClick = {
                        onDismiss()
                        onOpenShop()
                    },
                    brush = ButtonGoldGlassBrush,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🍭 Open Sweet Candy Shop", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Navigation Shortcut: Achievements
                GlassButton(
                    onClick = {
                        onDismiss()
                        onOpenAchievements()
                    },
                    brush = ButtonCyanGlassBrush,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🏆 View Achievements & Trophies", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Navigation Shortcut: How to Play
                GlassButton(
                    onClick = {
                        onDismiss()
                        onOpenHowToPlay()
                    },
                    brush = Brush.linearGradient(listOf(Color(0xFF8A2387), Color(0xFFE94057))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📖 How to Play & Special Candy Guide", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // App Info & Version
                Text(
                    "Candy Crush Glass v1.0.4",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SettingItemCard(
    icon: String,
    title: String,
    subtitle: String,
    action: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x28000000))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(subtitle, color = TextMuted, fontSize = 11.sp)
                }
            }
            action()
        }
    }
}

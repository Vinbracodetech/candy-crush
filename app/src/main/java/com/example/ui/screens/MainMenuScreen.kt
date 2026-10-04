package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
import com.example.ui.theme.NeonPurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun MainMenuScreen(
    profile: PlayerProfile,
    levelProgressList: List<LevelProgress> = emptyList(),
    isDailyLoginClaimable: Boolean = false,
    onPlayClick: () -> Unit,
    onOpenShop: () -> Unit = {},
    onOpenDailySpin: () -> Unit = {},
    onOpenDailyLogin: () -> Unit = {},
    onOpenGuide: () -> Unit = {},
    onOpenAchievements: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenFeedback: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val highestUnlockedLevel = levelProgressList.filter { it.isUnlocked }.maxOfOrNull { it.levelId } ?: 1
    val totalStars = levelProgressList.sumOf { it.stars }

    val infiniteTransition = rememberInfiniteTransition(label = "heroPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Top Status Glass Bar (Lives, Level, Coins, Settings)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xEE16072E),
                borderColor = NeonCyanSecondary.copy(alpha = 0.5f),
                elevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lives Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x35FF2A85))
                            .border(1.dp, NeonPinkPrimary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                            .clickable { onOpenShop() }
                    ) {
                        Text("❤️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${profile.lives}/${profile.maxLives}",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }

                    // Level & Star Progress
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text("⭐", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$totalStars", color = NeonGoldTertiary, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("• Lvl $highestUnlockedLevel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Coins Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x35FFD700))
                            .border(1.dp, NeonGoldTertiary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 11.dp, vertical = 5.dp)
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
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hero Brand Art & Title
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .scale(pulseScale)
                    .shadow(30.dp, RoundedCornerShape(26.dp), spotColor = NeonPinkPrimary)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFF1A0B2E),
                                Color(0xFF3B1578),
                                Color(0xFF0D2B66)
                            )
                        )
                    )
                    .border(3.5.dp, Color.White, RoundedCornerShape(26.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background layer of original icon
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
                // Foreground layer of original icon (glowing diamond candy jewel with sparkles)
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Candy Crush Glass Icon",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CANDY CRUSH",
                color = NeonGoldTertiary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "G L A S S",
                color = NeonCyanSecondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "✨ 350+ Neon Levels & Match-3 Puzzles ✨",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Primary Play Action Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onPlayClick),
                backgroundColor = Color(0x33FF2A85),
                borderColor = NeonPinkPrimary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("PLAY LEVEL $highestUnlockedLevel", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                        Text("Continue your sweet journey", color = NeonCyanSecondary, fontSize = 12.sp)
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonPinkPrimary, NeonPurpleAccent))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Daily Login Gift Calendar Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenDailyLogin),
                backgroundColor = if (isDailyLoginClaimable) Color(0x35FFD700) else Color(0x22FFFFFF),
                borderColor = if (isDailyLoginClaimable) NeonGoldTertiary else Color.White.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎁", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Daily Login Boosters", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                if (profile.dailyLoginStreak > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Day ${(profile.dailyLoginStreak - 1) % 7 + 1}/7", color = NeonCyanSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                            Text(
                                if (isDailyLoginClaimable) "Free daily hammer, swap, & bombs ready!" else "Streak: ${profile.dailyLoginStreak} Days • Check calendar",
                                color = if (isDailyLoginClaimable) NeonGoldTertiary else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (isDailyLoginClaimable) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonGreenAccent)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("CLAIM", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    } else {
                        Text("VIEW ›", color = NeonCyanSecondary, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Access Cards Grid (Shop & Lucky Spin)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sweet Shop Card
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenShop),
                    backgroundColor = Color(0x33FFD700),
                    borderColor = NeonGoldTertiary
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🍭", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Sweet Shop", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text("Coins & Moves", color = NeonGoldTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Daily Lucky Spin Card
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenDailySpin),
                    backgroundColor = Color(0x3300E676),
                    borderColor = NeonGreenAccent
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎡", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Daily Spin", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text("Free Rewards", color = NeonGreenAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Options Row (Achievements & How to play)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Achievements Card
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenAchievements),
                    backgroundColor = Color(0x2800C9FF),
                    borderColor = NeonCyanSecondary
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏆", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Trophies", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Milestones", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }

                // How to Play Guide Card
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenGuide),
                    backgroundColor = Color(0x289C27B0),
                    borderColor = NeonPurpleAccent
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📖", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("How to Play", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Combo Guide", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dedicated Player Rating & Feedback Card on Menu Page
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenFeedback),
                backgroundColor = Color(0x30FFD700),
                borderColor = NeonGoldTertiary.copy(alpha = 0.8f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Rate Game & Feedback", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            Text("Share your review on Google Play (+150 🪙)", color = NeonGoldTertiary, fontSize = 11.sp)
                        }
                    }
                    Text("RATE ›", color = NeonGoldTertiary, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dedicated Game Settings Card on Menu Page
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenSettings),
                backgroundColor = Color(0x28FFFFFF),
                borderColor = Color.White.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚙️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Game Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(if (profile.soundEnabled) "Sound Effects: ON • Vibration: ON" else "Sound Muted", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    Text("OPEN ›", color = NeonCyanSecondary, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(130.dp)) // Clearance for bottom dock & banner ad
        }

        // Bottom Navigation Bar on Opening Screen
        MainMenuBottomDock(
            onPlayClick = onPlayClick,
            onOpenShop = onOpenShop,
            onOpenDailySpin = onOpenDailySpin,
            onOpenAchievements = onOpenAchievements,
            onOpenGuide = onOpenGuide,
            onOpenSettings = onOpenSettings,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
        )
    }
}

@Composable
private fun MainMenuBottomDock(
    onPlayClick: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenDailySpin: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        backgroundColor = Color(0xF0180833),
        borderColor = NeonPinkPrimary.copy(alpha = 0.6f),
        elevation = 20.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Shop
            MenuDockTab(
                icon = "🍭",
                label = "Shop",
                badgeText = "COINS",
                badgeColor = NeonGoldTertiary,
                onClick = onOpenShop
            )

            // Tab 2: Spin
            MenuDockTab(
                icon = "🎡",
                label = "Spin",
                badgeText = "FREE",
                badgeColor = NeonGreenAccent,
                onClick = onOpenDailySpin
            )

            // Tab 3: Center Play Action
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .shadow(14.dp, CircleShape, spotColor = NeonPinkPrimary)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(NeonPinkPrimary, NeonPurpleAccent)))
                    .border(2.dp, Color.White, CircleShape)
                    .clickable(onClick = onPlayClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(32.dp))
            }

            // Tab 4: Trophies
            MenuDockTab(
                icon = "🏆",
                label = "Trophies",
                onClick = onOpenAchievements
            )

            // Tab 5: Guide
            MenuDockTab(
                icon = "📖",
                label = "Guide",
                onClick = onOpenGuide
            )
        }
    }
}

@Composable
private fun MenuDockTab(
    icon: String,
    label: String,
    badgeText: String? = null,
    badgeColor: Color = NeonPinkPrimary,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.TopEnd) {
                Text(icon, fontSize = 22.sp)
                if (badgeText != null) {
                    Box(
                        modifier = Modifier
                            .padding(start = 14.dp, bottom = 12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor)
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(badgeText, color = Color.Black, fontSize = 7.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}


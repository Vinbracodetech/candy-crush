package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ads.AdConfig
import com.example.ads.AdCreative
import com.example.ads.AdManager
import com.example.ads.AdMonetizationStats
import com.example.ads.AdType
import com.example.ads.RewardReason
import com.example.ui.theme.ButtonCyanGlassBrush
import com.example.ui.theme.ButtonGoldGlassBrush
import com.example.ui.theme.ButtonPinkGlassBrush
import com.example.ui.theme.DeepCosmicDark
import com.example.ui.theme.NeonCyanSecondary
import com.example.ui.theme.NeonGoldTertiary
import com.example.ui.theme.NeonGreenAccent
import com.example.ui.theme.NeonPinkPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PermanentBannerAd(
    currentAd: AdCreative?,
    onAdClick: (AdCreative) -> Unit = {},
    onOpenAdSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (currentAd == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .shadow(12.dp, RoundedCornerShape(14.dp), spotColor = Color(0x66000000))
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(currentAd.primaryColorHex).copy(alpha = 0.85f),
                        Color(currentAd.secondaryColorHex).copy(alpha = 0.85f),
                        Color(0xFF1F1235)
                    )
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.15f))),
                RoundedCornerShape(14.dp)
            )
            .clickable { onAdClick(currentAd) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App / Game Ad Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = currentAd.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Ad Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF00C853))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "AD",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentAd.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = currentAd.headline,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = NeonGoldTertiary,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${currentAd.rating} • ${currentAd.downloads}",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // CTA Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.horizontalGradient(listOf(NeonCyanSecondary, Color(0xFF0091EA))))
                    .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = currentAd.callToAction,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun FullscreenAdModal(
    adState: AdManager.ActiveAdState,
    onClose: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            if (adState.canSkip) onClose()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = adState.canSkip)
    ) {
        val creative = adState.creative
        var isInstalledDemo by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepCosmicDark)
        ) {
            // Ad Media Canvas / Video Header
            Column(modifier = Modifier.fillMaxSize()) {
                // Video & Playable Showcase Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(creative.primaryColorHex),
                                    Color(creative.secondaryColorHex),
                                    Color(0xFF140728)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .shadow(20.dp, CircleShape, spotColor = Color.White)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f))
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = creative.iconEmoji, fontSize = 52.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = creative.title,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = creative.headline,
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Star, "Rating", tint = NeonGoldTertiary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${creative.rating}", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("•  ${creative.downloads} Downloads", color = TextSecondary, fontSize = 13.sp)
                        }
                    }

                    // Progress Bar for Ad Duration
                    val progress = if (adState.totalSeconds > 0) {
                        (adState.totalSeconds - adState.countdownSeconds).toFloat() / adState.totalSeconds.toFloat()
                    } else 1f
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .align(Alignment.BottomCenter),
                        color = NeonGreenAccent,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                }

                // Ad Lower Details and CTA Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF180A2E))
                        .padding(20.dp)
                ) {
                    Column {
                        // Reward Granted Banner if applicable
                        if (adState.rewardReason != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (adState.isCompleted) Color(0x3300E676) else Color(0x22FFFFFF))
                                    .border(1.dp, if (adState.isCompleted) NeonGreenAccent else Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Reward",
                                    tint = if (adState.isCompleted) NeonGreenAccent else NeonGoldTertiary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (adState.isCompleted) "REWARD GRANTED!" else "REWARD IN PROGRESS",
                                        color = if (adState.isCompleted) NeonGreenAccent else NeonGoldTertiary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = adState.rewardReason.rewardDescription,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        Text(
                            text = creative.description,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Large CTA Install Button
                        GlassButton(
                            onClick = { isInstalledDemo = true },
                            brush = ButtonPinkGlassBrush,
                            modifier = Modifier.fillMaxWidth(),
                            padding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp)
                        ) {
                            Text(
                                text = if (isInstalledDemo) "✓ OPENED STORE LINK" else "${creative.callToAction} - FREE",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Top Header: Ad Badge & Close / Countdown Timer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ad Type Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    val label = when (adState.adType) {
                        AdType.REWARDED -> "Rewarded Ad"
                        AdType.REWARDED_INTERSTITIAL -> "VIP Rewarded Interstitial"
                        AdType.APP_OPEN -> "App Launch"
                        else -> "Sponsored Ad"
                    }
                    Text(text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Countdown / Close Button
                if (adState.canSkip) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.75f))
                            .border(1.5.dp, Color.White, CircleShape)
                            .clickable { onClose() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close Ad", tint = Color.White)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.7f))
                            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Reward in ${adState.countdownSeconds}s",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdMonetizationDashboardDialog(
    stats: AdMonetizationStats,
    config: AdConfig,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = Color(0xDD1B0C33)
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
                    Text(
                        text = "Monetization Suite",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Estimated Revenue Card
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
                        Text(text = "ESTIMATED REVENUE", color = NeonGreenAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = String.format("$%.3f", stats.estimatedEarningsUsd),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats breakdown
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatBox("Banners", "${stats.bannerImpressions}", NeonCyanSecondary, Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    StatBox("Interstitials", "${stats.interstitialImpressions}", NeonPinkPrimary, Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    StatBox("Rewarded", "${stats.rewardedImpressions}", NeonGoldTertiary, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // AdMob Unit IDs
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(10.dp)
                ) {
                    Text("AdMob Ad Unit Config:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Banner: ${config.bannerAdUnitId}", color = TextMuted, fontSize = 9.sp, maxLines = 1)
                    Text("• Rewarded: ${config.rewardedAdUnitId}", color = TextMuted, fontSize = 9.sp, maxLines = 1)
                    Text("• Interstitial: ${config.interstitialAdUnitId}", color = TextMuted, fontSize = 9.sp, maxLines = 1)
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlassButton(
                    onClick = onDismiss,
                    brush = ButtonCyanGlassBrush,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close Panel", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatBox(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = TextMuted, fontSize = 10.sp)
            Text(text = value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

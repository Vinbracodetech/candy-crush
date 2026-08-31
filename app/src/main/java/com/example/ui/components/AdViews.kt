package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.ads.AdConfig
import com.example.ads.AdMonetizationStats
import com.example.ui.theme.NeonCyanSecondary
import com.example.ui.theme.NeonGoldTertiary
import com.example.ui.theme.NeonGreenAccent
import com.example.ui.theme.NeonPinkPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ButtonCyanGlassBrush
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun PermanentBannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .navigationBarsPadding()
            .clip(RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(60.dp),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
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

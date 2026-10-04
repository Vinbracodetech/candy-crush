package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
fun FeedbackRatingDialog(
    currentLevelReached: Int = 1,
    onRewardCoins: (Int) -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedStars by remember { mutableIntStateOf(5) }
    var feedbackText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Puzzle Fun") }
    var isSubmitted by remember { mutableStateOf(false) }

    val feedbackTags = listOf("Puzzle Fun", "Graphics & Audio", "Level Difficulty", "Boosters", "New Features", "Bug Report")

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            backgroundColor = Color(0xF2180833),
            borderColor = NeonGoldTertiary,
            elevation = 20.dp
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
                        Text("⭐", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "PLAYER FEEDBACK",
                                color = NeonGoldTertiary,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                "Level $currentLevelReached Milestone ✨",
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

                Spacer(modifier = Modifier.height(14.dp))

                if (!isSubmitted) {
                    Text(
                        "Enjoying Candy Crush Glass? How would you rate your experience?",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5 Interactive Star Rating Selector
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x35000000))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        for (star in 1..5) {
                            val isFilled = star <= selectedStars
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$star Star",
                                tint = if (isFilled) NeonGoldTertiary else Color.White.copy(alpha = 0.25f),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable { selectedStars = star }
                                    .padding(3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when (selectedStars) {
                            5 -> "⭐⭐⭐⭐⭐ Outstanding! Perfect Match-3!"
                            4 -> "⭐⭐⭐⭐ Great game! Very fun!"
                            3 -> "⭐⭐⭐ Good, but needs improvements"
                            2 -> "⭐⭐ Fair, challenging levels"
                            else -> "⭐ Needs work"
                        },
                        color = NeonGoldTertiary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Category Pill Tags
                    Text(
                        "Feedback Topic:",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        feedbackTags.take(3).forEach { tag ->
                            FeedbackTagPill(
                                title = tag,
                                isSelected = selectedCategory == tag,
                                onClick = { selectedCategory = tag },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        feedbackTags.drop(3).take(3).forEach { tag ->
                            FeedbackTagPill(
                                title = tag,
                                isSelected = selectedCategory == tag,
                                onClick = { selectedCategory = tag },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Feedback Comment Input
                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        placeholder = {
                            Text(
                                "Tell us your thoughts, favorite levels, or ideas...",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyanSecondary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedContainerColor = Color(0x35000000),
                            unfocusedContainerColor = Color(0x25000000),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action 1: Rate & Review on Google Play Store
                    GlassButton(
                        onClick = {
                            openPlayStore(context)
                            onRewardCoins(150)
                            isSubmitted = true
                        },
                        brush = ButtonGoldGlassBrush,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.RateReview, "Play Store", tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "RATE ON PLAY STORE (+150 🪙)",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action 2: Submit In-App Feedback Directly
                    GlassButton(
                        onClick = {
                            submitInAppFeedback(
                                context = context,
                                stars = selectedStars,
                                category = selectedCategory,
                                comment = feedbackText,
                                level = currentLevelReached
                            )
                            onRewardCoins(100)
                            isSubmitted = true
                        },
                        brush = ButtonCyanGlassBrush,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Send, "Send", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "SUBMIT FEEDBACK (+100 🪙)",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    // Success Thank You Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x3500E676))
                            .border(1.5.dp, NeonGreenAccent, RoundedCornerShape(16.dp))
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎉", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "THANK YOU SO MUCH!",
                                color = NeonGreenAccent,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Your rating & feedback helps make Candy Crush Glass even sweeter! We've rewarded free bonus coins into your bank.",
                                color = Color.White,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🪙 +Coins Claimed!", color = NeonGoldTertiary, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    GlassButton(
                        onClick = onDismiss,
                        brush = ButtonPinkGlassBrush,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("BACK TO GAME", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedbackTagPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0x66FF2A85) else Color(0x25FFFFFF))
            .border(
                width = 1.dp,
                color = if (isSelected) NeonPinkPrimary else Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

private fun openPlayStore(context: Context) {
    val packageName = context.packageName
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(webIntent)
    }
}

private fun submitInAppFeedback(
    context: Context,
    stars: Int,
    category: String,
    comment: String,
    level: Int
) {
    Toast.makeText(context, "Thank you! Feedback saved ⭐⭐⭐⭐⭐", Toast.LENGTH_SHORT).show()
    // Open email intent as fallback if user has long feedback
    if (comment.isNotBlank()) {
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@candycrushglass.com")
                putExtra(Intent.EXTRA_SUBJECT, "Candy Crush Glass Feedback (Lvl $level - $stars Stars)")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Rating: $stars/5 Stars\nTopic: $category\nLevel: $level\nFeedback:\n$comment"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (emailIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(emailIntent)
            }
        } catch (_: Exception) {}
    }
}

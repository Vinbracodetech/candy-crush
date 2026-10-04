package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.RewardReason
import com.example.ui.components.GlassBackground
import com.example.ui.components.PermanentBannerAd
import com.example.ui.screens.BoosterShopDialog
import com.example.ui.screens.DailyLuckyWheelDialog
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.LevelMapScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.ScreenState

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.adManager.init(this)
        viewModel.adManager.currentActivity = this
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CandyCrushGlassApp(viewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (viewModel.adManager.currentActivity == this) {
            viewModel.adManager.currentActivity = null
        }
    }
}

@Composable
fun CandyCrushGlassApp(viewModel: GameViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val profile by viewModel.playerProfile.collectAsStateWithLifecycle()
    val levelProgressList by viewModel.levelProgressList.collectAsStateWithLifecycle()
    val adConfig by viewModel.adManager.config.collectAsStateWithLifecycle()
    val showShop by viewModel.showShopDialog.collectAsStateWithLifecycle()
    val showSpin by viewModel.showDailySpinDialog.collectAsStateWithLifecycle()
    val currentConfig by viewModel.currentLevelConfig.collectAsStateWithLifecycle()
    val engineState by viewModel.engineState.collectAsStateWithLifecycle()
    val showSettings by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showHowToPlay by viewModel.showHowToPlayDialog.collectAsStateWithLifecycle()
    val showAchievements by viewModel.showAchievementsDialog.collectAsStateWithLifecycle()
    val showFeedback by viewModel.showFeedbackDialog.collectAsStateWithLifecycle()
    val showDailyLogin by viewModel.showDailyLoginDialog.collectAsStateWithLifecycle()
    val isLoginClaimable by viewModel.isDailyLoginClaimable.collectAsStateWithLifecycle()

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Screen Routing
            Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                when (screen) {
                    is ScreenState.MainMenu -> {
                        MainMenuScreen(
                            profile = profile,
                            levelProgressList = levelProgressList,
                            isDailyLoginClaimable = isLoginClaimable,
                            onPlayClick = { viewModel.navigateToMap() },
                            onOpenShop = { viewModel.showShopDialog.value = true },
                            onOpenDailySpin = { viewModel.showDailySpinDialog.value = true },
                            onOpenDailyLogin = { viewModel.showDailyLoginDialog.value = true },
                            onOpenGuide = { viewModel.showHowToPlayDialog.value = true },
                            onOpenAchievements = { viewModel.showAchievementsDialog.value = true },
                            onOpenSettings = { viewModel.showSettingsDialog.value = true },
                            onOpenFeedback = { viewModel.showFeedbackDialog.value = true }
                        )
                    }
                    is ScreenState.LevelMap -> {
                        LevelMapScreen(
                            profile = profile,
                            levelProgressList = levelProgressList,
                            isDailyLoginClaimable = isLoginClaimable,
                            onSelectLevel = { levelNum, useVipColorBomb ->
                                viewModel.startLevel(levelNum, useVipColorBomb)
                            },
                            onOpenShop = { viewModel.showShopDialog.value = true },
                            onOpenDailySpin = { viewModel.showDailySpinDialog.value = true },
                            onOpenDailyLogin = { viewModel.showDailyLoginDialog.value = true },
                            onOpenMonetizationStats = { /* Removed */ },
                            onOpenSettings = { viewModel.showSettingsDialog.value = true },
                            onOpenHowToPlay = { viewModel.showHowToPlayDialog.value = true },
                            onOpenAchievements = { viewModel.showAchievementsDialog.value = true },
                            onOpenFeedback = { viewModel.showFeedbackDialog.value = true },
                            onBackToMainMenu = { viewModel.navigateToMainMenu() },
                            onWatchAdForLives = { viewModel.watchAdForLives() },
                            onToggleSound = { viewModel.toggleSound() }
                        )
                    }
                    is ScreenState.Playing -> {
                        val config = currentConfig
                        val state = engineState
                        if (config != null && state != null) {
                            GamePlayScreen(
                                levelConfig = config,
                                engineState = state,
                                profile = profile,
                                onTileClick = { r, c -> viewModel.onTileClick(r, c) },
                                onSelectBooster = { booster -> viewModel.selectBooster(booster) },
                                onWatchAdForExtraMoves = { viewModel.watchAdForExtraMoves() },
                                onBuyMovesWithCoins = { viewModel.buyExtraMovesWithCoins(100, 15) },
                                onNextLevel = { viewModel.nextLevel() },
                                onRetryLevel = { viewModel.retryLevel() },
                                onExitToMap = { viewModel.exitToMap() },
                                onOpenFeedback = { viewModel.showFeedbackDialog.value = true },
                                onToggleSound = { viewModel.toggleSound() }
                            )
                        }
                    }
                }
            }

            // Permanent Bottom Banner Ad
            PermanentBannerAd(
                adUnitId = adConfig.bannerAdUnitId,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (showShop) {
            BoosterShopDialog(
                profile = profile,
                getCooldownSeconds = { reason -> viewModel.adManager.getRemainingRewardedCooldownSeconds(reason) },
                onBuyBooster = { type, cost -> viewModel.buyBoosterWithCoins(type, cost) },
                onBuyLives = { cost -> viewModel.buyLivesWithCoins(cost, 5) },
                onWatchAdForReward = { reason -> viewModel.watchAdForReward(reason) },
                onDismiss = { viewModel.showShopDialog.value = false }
            )
        }

        val userMsg by viewModel.userMessage.collectAsStateWithLifecycle()
        if (userMsg != null) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { viewModel.dismissUserMessage() }) {
                com.example.ui.components.GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    backgroundColor = androidx.compose.ui.graphics.Color(0xEE1E0B38),
                    borderColor = com.example.ui.theme.NeonGoldTertiary
                ) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.material3.Text(
                            text = userMsg ?: "",
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                        com.example.ui.components.GlassButton(
                            onClick = { viewModel.dismissUserMessage() },
                            brush = com.example.ui.theme.ButtonCyanGlassBrush,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.Text(
                                "OK",
                                color = androidx.compose.ui.graphics.Color.White,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        if (showSpin) {
            DailyLuckyWheelDialog(
                onRewardWon = { rewardText, coins, hammer, swap, bomb ->
                    viewModel.claimDailyReward(rewardText, coins, hammer, swap, bomb)
                },
                onWatchAdForExtraSpin = {
                    viewModel.watchAdForReward(RewardReason.DAILY_BONUS_SPIN)
                },
                onDismiss = { viewModel.showDailySpinDialog.value = false }
            )
        }

        if (showSettings) {
            com.example.ui.screens.GameSettingsDialog(
                profile = profile,
                onToggleSound = { viewModel.toggleSound() },
                onOpenHowToPlay = {
                    viewModel.showSettingsDialog.value = false
                    viewModel.showHowToPlayDialog.value = true
                },
                onOpenAchievements = {
                    viewModel.showSettingsDialog.value = false
                    viewModel.showAchievementsDialog.value = true
                },
                onOpenShop = {
                    viewModel.showSettingsDialog.value = false
                    viewModel.showShopDialog.value = true
                },
                onOpenFeedback = {
                    viewModel.showSettingsDialog.value = false
                    viewModel.showFeedbackDialog.value = true
                },
                onDismiss = { viewModel.showSettingsDialog.value = false }
            )
        }

        if (showHowToPlay) {
            com.example.ui.screens.HowToPlayDialog(
                onDismiss = { viewModel.showHowToPlayDialog.value = false }
            )
        }

        if (showAchievements) {
            com.example.ui.screens.AchievementsDialog(
                profile = profile,
                levelProgressList = levelProgressList,
                onDismiss = { viewModel.showAchievementsDialog.value = false }
            )
        }

        if (showFeedback) {
            val maxLevel = levelProgressList.maxOfOrNull { it.levelId } ?: (currentConfig?.levelNumber ?: 1)
            com.example.ui.screens.FeedbackRatingDialog(
                currentLevelReached = maxLevel,
                onRewardCoins = { coins -> viewModel.rewardFeedbackCoins(coins) },
                onDismiss = { viewModel.showFeedbackDialog.value = false }
            )
        }

        if (showDailyLogin) {
            com.example.ui.screens.DailyLoginRewardDialog(
                currentStreak = profile.dailyLoginStreak,
                isClaimableToday = isLoginClaimable,
                onClaimReward = { reward -> viewModel.claimDailyLoginReward(reward) },
                onDismiss = { viewModel.showDailyLoginDialog.value = false }
            )
        }
    }
}

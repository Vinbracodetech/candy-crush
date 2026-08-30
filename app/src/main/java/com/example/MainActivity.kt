package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.RewardReason
import com.example.ui.components.AdMonetizationDashboardDialog
import com.example.ui.components.GlassBackground
import com.example.ui.components.PermanentBannerAd
import com.example.ui.screens.BoosterShopDialog
import com.example.ui.screens.DailyLuckyWheelDialog
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.LevelMapScreen
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
    val currentBannerAd by viewModel.adManager.currentBannerAd.collectAsStateWithLifecycle()
    val activeAdState by viewModel.adManager.activeAd.collectAsStateWithLifecycle()
    val adStats by viewModel.adManager.adStats.collectAsStateWithLifecycle()
    val adConfig by viewModel.adManager.config.collectAsStateWithLifecycle()

    val showShop by viewModel.showShopDialog.collectAsStateWithLifecycle()
    val showSpin by viewModel.showDailySpinDialog.collectAsStateWithLifecycle()
    val showStats by viewModel.showMonetizationDialog.collectAsStateWithLifecycle()

    val currentConfig by viewModel.currentLevelConfig.collectAsStateWithLifecycle()
    val engineState by viewModel.engineState.collectAsStateWithLifecycle()

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Screen Routing
            Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                when (screen) {
                    is ScreenState.LevelMap -> {
                        LevelMapScreen(
                            profile = profile,
                            levelProgressList = levelProgressList,
                            onSelectLevel = { levelNum, useVipColorBomb ->
                                viewModel.startLevel(levelNum, useVipColorBomb)
                            },
                            onOpenShop = { viewModel.showShopDialog.value = true },
                            onOpenDailySpin = { viewModel.showDailySpinDialog.value = true },
                            onOpenMonetizationStats = { viewModel.showMonetizationDialog.value = true },
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
                                onNextLevel = { viewModel.nextLevel() },
                                onRetryLevel = { viewModel.retryLevel() },
                                onExitToMap = { viewModel.exitToMap() },
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

        // Active ad state is now managed completely by native Google AdMob SDK.
        // Fullscreen active ad modals are no longer simulated in Compose.

        // Booster Shop & Lives Dialog
        if (showShop) {
            BoosterShopDialog(
                profile = profile,
                onBuyBooster = { type, cost -> viewModel.buyBoosterWithCoins(type, cost) },
                onWatchAdForReward = { reason -> viewModel.watchAdForReward(reason) },
                onDismiss = { viewModel.showShopDialog.value = false }
            )
        }

        // Daily Lucky Wheel Dialog
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

        // Ad Monetization & Stats Panel Dialog
        if (showStats) {
            AdMonetizationDashboardDialog(
                stats = adStats,
                config = adConfig,
                onDismiss = { viewModel.showMonetizationDialog.value = false }
            )
        }
    }
}

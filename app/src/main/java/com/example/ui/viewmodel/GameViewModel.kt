package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.ads.AdConfig
import com.example.ads.AdCreative
import com.example.ads.AdManager
import com.example.ads.AdMonetizationStats
import com.example.ads.RewardReason
import com.example.audio.SoundSynth
import com.example.data.GameRepository
import com.example.data.local.AppDatabase
import com.example.data.local.LevelProgress
import com.example.data.local.PlayerProfile
import com.example.data.model.LevelConfig
import com.example.engine.LevelGenerator
import com.example.engine.Match3Engine
import com.example.engine.MatchEngineState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenState {
    object MainMenu : ScreenState()
    object LevelMap : ScreenState()
    data class Playing(val levelNumber: Int) : ScreenState()
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "candy_crush_glass.db"
    ).build()

    val repository = GameRepository(db.levelProgressDao(), db.playerProfileDao())
    val adManager = AdManager()

    val playerProfile: StateFlow<PlayerProfile> = repository.playerProfile
        .map { it ?: PlayerProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerProfile()
        )

    val levelProgressList: StateFlow<List<LevelProgress>> = repository.allLevelProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val soundSynth = SoundSynth { playerProfile.value.soundEnabled }

    // Screen State
    private val _currentScreen = MutableStateFlow<ScreenState>(ScreenState.MainMenu)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    // Match 3 Engine & State
    private var engine: Match3Engine? = null
    private val _currentLevelConfig = MutableStateFlow<LevelConfig?>(null)
    val currentLevelConfig: StateFlow<LevelConfig?> = _currentLevelConfig.asStateFlow()

    private val _engineState = MutableStateFlow<MatchEngineState?>(null)
    val engineState: StateFlow<MatchEngineState?> = _engineState.asStateFlow()

    // Dialogs State
    var showShopDialog = MutableStateFlow(false)
    var showDailySpinDialog = MutableStateFlow(false)
    var showMonetizationDialog = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.initializeIfEmpty()
            repository.checkAndRegenerateLives()
        }

        // Show App Open Ad on launch after brief delay
        viewModelScope.launch {
            delay(1200)
            adManager.showAppOpenAd()
        }

        // Periodic lives regeneration check
        viewModelScope.launch {
            while (true) {
                delay(30000)
                repository.checkAndRegenerateLives()
            }
        }
    }

    fun startLevel(levelNumber: Int, useVipColorBomb: Boolean = false) {
        viewModelScope.launch {
            val hasLife = repository.deductLife()
            if (!hasLife) {
                // Prompt ad for lives
                adManager.showRewardedAd(
                    reason = RewardReason.EXTRA_LIVES,
                    onRewardGranted = {
                        viewModelScope.launch {
                            repository.restoreLives(5)
                        }
                    }
                )
                return@launch
            }

            if (useVipColorBomb) {
                // Show rewarded interstitial for VIP boost
                adManager.showRewardedInterstitial(
                    reason = RewardReason.COLOR_BOMB_START,
                    onRewardGranted = {
                        launchEngine(levelNumber, startWithColorBomb = true)
                    },
                    onDismiss = {
                        launchEngine(levelNumber, startWithColorBomb = false)
                    }
                )
            } else {
                launchEngine(levelNumber, startWithColorBomb = false)
            }
        }
    }

    private fun launchEngine(levelNumber: Int, startWithColorBomb: Boolean) {
        val config = LevelGenerator.getLevel(levelNumber)
        _currentLevelConfig.value = config

        engine = Match3Engine(
            config = config,
            onSound = { sound, combo ->
                when (sound) {
                    "swap" -> soundSynth.playSwap()
                    "invalid" -> soundSynth.playInvalidSwap()
                    "match" -> soundSynth.playMatch(combo)
                    "line" -> soundSynth.playLineBlast()
                    "bomb" -> soundSynth.playBombExplosion()
                    "color_bomb" -> soundSynth.playColorBomb()
                    "win" -> soundSynth.playWinFanfare()
                    "game_over" -> soundSynth.playGameOver()
                    "click" -> soundSynth.playButtonClick()
                }
            }
        )

        if (startWithColorBomb) {
            engine?.activeBooster = "bomb"
        }

        _engineState.value = engine?.getState()
        _currentScreen.value = ScreenState.Playing(levelNumber)
    }

    fun onTileClick(row: Int, col: Int) {
        viewModelScope.launch {
            engine?.onTileClicked(row, col) { newState ->
                _engineState.value = newState
            }

            // Check Win Condition
            val state = engine?.getState()
            if (state?.isGameWon == true) {
                val lvl = _currentLevelConfig.value?.levelNumber ?: 1
                val stars = when {
                    state.currentScore >= (_currentLevelConfig.value?.targetScore3Star ?: 0) -> 3
                    state.currentScore >= (_currentLevelConfig.value?.targetScore2Star ?: 0) -> 2
                    else -> 1
                }
                repository.completeLevel(lvl, stars, state.currentScore)
            }
        }
    }

    fun selectBooster(type: String) {
        viewModelScope.launch {
            val used = repository.useBooster(type)
            if (used) {
                soundSynth.playBoosterActivate()
                engine?.activeBooster = type
                _engineState.value = engine?.getState()
            } else {
                // Open shop / rewarded ad prompt
                showShopDialog.value = true
            }
        }
    }

    fun watchAdForExtraMoves() {
        adManager.showRewardedAd(
            reason = RewardReason.CONTINUE_LEVEL,
            onRewardGranted = {
                engine?.addExtraMoves(5)
                _engineState.value = engine?.getState()
            }
        )
    }

    fun watchAdForLives() {
        adManager.showRewardedAd(
            reason = RewardReason.EXTRA_LIVES,
            onRewardGranted = {
                viewModelScope.launch {
                    repository.restoreLives(5)
                }
            }
        )
    }

    fun watchAdForReward(reason: RewardReason) {
        adManager.showRewardedAd(
            reason = reason,
            onRewardGranted = { r ->
                viewModelScope.launch {
                    when (r) {
                        RewardReason.EXTRA_LIVES -> repository.restoreLives(5)
                        RewardReason.FREE_HAMMER -> repository.addBooster("hammer", 1)
                        RewardReason.FREE_SWAP -> repository.addBooster("swap", 1)
                        RewardReason.COLOR_BOMB_START -> repository.addBooster("bomb", 1)
                        RewardReason.CONTINUE_LEVEL -> engine?.addExtraMoves(5)
                        RewardReason.DAILY_BONUS_SPIN -> showDailySpinDialog.value = true
                    }
                    _engineState.value = engine?.getState()
                }
            }
        )
    }

    fun buyBoosterWithCoins(type: String, cost: Int) {
        viewModelScope.launch {
            val profile = playerProfile.value
            if (profile.coins >= cost) {
                repository.addCoins(-cost)
                repository.addBooster(type, 1)
            }
        }
    }

    fun claimDailyReward(rewardText: String, coins: Int, hammer: Int, swap: Int, bomb: Int) {
        viewModelScope.launch {
            if (coins > 0) repository.addCoins(coins)
            if (hammer > 0) repository.addBooster("hammer", hammer)
            if (swap > 0) repository.addBooster("swap", swap)
            if (bomb > 0) repository.addBooster("bomb", bomb)
        }
    }

    fun nextLevel() {
        val currentLvl = _currentLevelConfig.value?.levelNumber ?: 1
        if (adManager.shouldShowInterstitial()) {
            adManager.showInterstitial(
                onDismiss = {
                    startLevel(currentLvl + 1)
                }
            )
        } else {
            startLevel(currentLvl + 1)
        }
    }

    fun retryLevel() {
        val currentLvl = _currentLevelConfig.value?.levelNumber ?: 1
        if (adManager.shouldShowInterstitial()) {
            adManager.showInterstitial(
                onDismiss = {
                    startLevel(currentLvl)
                }
            )
        } else {
            startLevel(currentLvl)
        }
    }

    fun navigateToMap() {
        _currentScreen.value = ScreenState.LevelMap
    }

    fun navigateToMainMenu() {
        _currentScreen.value = ScreenState.MainMenu
    }

    fun exitToMap() {
        if (adManager.shouldShowInterstitial()) {
            adManager.showInterstitial(
                onDismiss = {
                    _currentScreen.value = ScreenState.LevelMap
                }
            )
        } else {
            _currentScreen.value = ScreenState.LevelMap
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            val current = playerProfile.value
            repository.updateProfile { it.copy(soundEnabled = !current.soundEnabled) }
        }
    }
}

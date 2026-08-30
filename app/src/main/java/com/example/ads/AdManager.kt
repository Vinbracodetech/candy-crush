package com.example.ads

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdManager {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _adStats = MutableStateFlow(AdMonetizationStats())
    val adStats: StateFlow<AdMonetizationStats> = _adStats.asStateFlow()

    private val _config = MutableStateFlow(AdConfig())
    val config: StateFlow<AdConfig> = _config.asStateFlow()

    // Current active banner ad
    private val _currentBannerAd = MutableStateFlow<AdCreative?>(null)
    val currentBannerAd: StateFlow<AdCreative?> = _currentBannerAd.asStateFlow()

    // Full-screen Ad display state
    data class ActiveAdState(
        val isVisible: Boolean = false,
        val adType: AdType = AdType.INTERSTITIAL,
        val creative: AdCreative = SAMPLE_CREATIVES[0],
        val rewardReason: RewardReason? = null,
        val countdownSeconds: Int = 5,
        val totalSeconds: Int = 15,
        val canSkip: Boolean = false,
        val isCompleted: Boolean = false
    )

    private val _activeAd = MutableStateFlow<ActiveAdState?>(null)
    val activeAd: StateFlow<ActiveAdState?> = _activeAd.asStateFlow()

    private var bannerTimerJob: Job? = null
    private var activeAdTimerJob: Job? = null
    private var pendingRewardCallback: ((RewardReason) -> Unit)? = null
    private var onAdDismissedCallback: (() -> Unit)? = null

    private var levelsSinceLastInterstitial = 0

    init {
        startBannerRotation()
    }

    private fun startBannerRotation() {
        bannerTimerJob?.cancel()
        bannerTimerJob = scope.launch {
            var index = 0
            while (true) {
                val creative = SAMPLE_CREATIVES[index % SAMPLE_CREATIVES.size]
                _currentBannerAd.value = creative
                _adStats.update {
                    it.copy(
                        bannerImpressions = it.bannerImpressions + 1,
                        estimatedEarningsUsd = it.estimatedEarningsUsd + 0.0018 // ~$1.80 eCPM
                    )
                }
                index++
                delay(_config.value.bannerRefreshIntervalSec * 1000L)
            }
        }
    }

    fun showAppOpenAd(onDismiss: () -> Unit = {}) {
        val creative = SAMPLE_CREATIVES.random()
        onAdDismissedCallback = onDismiss
        _activeAd.value = ActiveAdState(
            isVisible = true,
            adType = AdType.APP_OPEN,
            creative = creative,
            countdownSeconds = 3,
            totalSeconds = 3,
            canSkip = false
        )
        _adStats.update {
            it.copy(
                appOpenImpressions = it.appOpenImpressions + 1,
                estimatedEarningsUsd = it.estimatedEarningsUsd + 0.006 // ~$6.00 eCPM
            )
        }
        startAdCountdown(3, 3, isRewarded = false)
    }

    fun shouldShowInterstitial(): Boolean {
        levelsSinceLastInterstitial++
        return levelsSinceLastInterstitial >= _config.value.interstitialFrequencyLevels
    }

    fun showInterstitial(onDismiss: () -> Unit) {
        levelsSinceLastInterstitial = 0
        val creative = SAMPLE_CREATIVES.random()
        onAdDismissedCallback = onDismiss
        _activeAd.value = ActiveAdState(
            isVisible = true,
            adType = AdType.INTERSTITIAL,
            creative = creative,
            countdownSeconds = 5,
            totalSeconds = 15,
            canSkip = false
        )
        _adStats.update {
            it.copy(
                interstitialImpressions = it.interstitialImpressions + 1,
                estimatedEarningsUsd = it.estimatedEarningsUsd + 0.0085 // ~$8.50 eCPM
            )
        }
        startAdCountdown(5, 15, isRewarded = false)
    }

    fun showRewardedAd(reason: RewardReason, onRewardGranted: (RewardReason) -> Unit, onDismiss: () -> Unit = {}) {
        val creative = SAMPLE_CREATIVES.random()
        pendingRewardCallback = onRewardGranted
        onAdDismissedCallback = onDismiss
        val duration = creative.videoDurationSec
        _activeAd.value = ActiveAdState(
            isVisible = true,
            adType = AdType.REWARDED,
            creative = creative,
            rewardReason = reason,
            countdownSeconds = duration,
            totalSeconds = duration,
            canSkip = false
        )
        _adStats.update {
            it.copy(
                rewardedImpressions = it.rewardedImpressions + 1,
                estimatedEarningsUsd = it.estimatedEarningsUsd + 0.024 // ~$24.00 eCPM
            )
        }
        startAdCountdown(duration, duration, isRewarded = true)
    }

    fun showRewardedInterstitial(reason: RewardReason, onRewardGranted: (RewardReason) -> Unit, onDismiss: () -> Unit = {}) {
        val creative = SAMPLE_CREATIVES.random()
        pendingRewardCallback = onRewardGranted
        onAdDismissedCallback = onDismiss
        _activeAd.value = ActiveAdState(
            isVisible = true,
            adType = AdType.REWARDED_INTERSTITIAL,
            creative = creative,
            rewardReason = reason,
            countdownSeconds = 8,
            totalSeconds = 8,
            canSkip = false
        )
        _adStats.update {
            it.copy(
                rewardedImpressions = it.rewardedImpressions + 1,
                estimatedEarningsUsd = it.estimatedEarningsUsd + 0.018 // ~$18.00 eCPM
            )
        }
        startAdCountdown(8, 8, isRewarded = true)
    }

    private fun startAdCountdown(skipCountdown: Int, totalDuration: Int, isRewarded: Boolean) {
        activeAdTimerJob?.cancel()
        activeAdTimerJob = scope.launch {
            var currentSec = skipCountdown
            while (currentSec > 0) {
                delay(1000L)
                currentSec--
                _activeAd.update { state ->
                    state?.copy(
                        countdownSeconds = currentSec,
                        canSkip = if (isRewarded) currentSec == 0 else currentSec == 0
                    )
                }
            }
            if (isRewarded) {
                _activeAd.update { it?.copy(isCompleted = true) }
            }
        }
    }

    fun closeActiveAd() {
        activeAdTimerJob?.cancel()
        val current = _activeAd.value
        val reason = current?.rewardReason
        val isCompleted = current?.isCompleted == true || current?.countdownSeconds == 0

        _activeAd.value = null

        if (reason != null && isCompleted) {
            pendingRewardCallback?.invoke(reason)
        }
        pendingRewardCallback = null

        onAdDismissedCallback?.invoke()
        onAdDismissedCallback = null
    }

    fun updateConfig(transform: (AdConfig) -> AdConfig) {
        _config.update(transform)
    }

    companion object {
        val SAMPLE_CREATIVES = listOf(
            AdCreative(
                id = "ad_game_1",
                title = "Dragon Blast 3D",
                headline = "Match & Blast Mystical Dragons!",
                description = "Join millions of players in the epic puzzle adventure. 500+ magical levels await you. Play Free!",
                callToAction = "Install Now",
                sponsorName = "Mythic Play Studios",
                rating = 4.8f,
                downloads = "10M+",
                category = "Puzzle & Adventure",
                primaryColorHex = 0xFFFF416C,
                secondaryColorHex = 0xFFFF4B2B,
                iconEmoji = "🐉",
                videoDurationSec = 15
            ),
            AdCreative(
                id = "ad_fintech_2",
                title = "Neon Bank & Crypto",
                headline = "Zero Fee Global Transfers & Rewards",
                description = "Get 5% cash back on all online gaming purchases. Smart, fast, secure mobile banking with zero maintenance fees.",
                callToAction = "Claim $20 Bonus",
                sponsorName = "Neon Fintech Global",
                rating = 4.9f,
                downloads = "5M+",
                category = "Finance",
                primaryColorHex = 0xFF8A2387,
                secondaryColorHex = 0xFFE94057,
                iconEmoji = "💳",
                videoDurationSec = 12
            ),
            AdCreative(
                id = "ad_rpg_3",
                title = "Cyber Odyssey: 2099",
                headline = "Next-Gen Open World Action RPG",
                description = "Stunning ray-traced graphics on mobile. Collect legendary heroes and conquer the cybernetic metropolis!",
                callToAction = "Play Free",
                sponsorName = "Hyperion Interactive",
                rating = 4.7f,
                downloads = "20M+",
                category = "Action RPG",
                primaryColorHex = 0xFF00C9FF,
                secondaryColorHex = 0xFF92FE9D,
                iconEmoji = "🤖",
                videoDurationSec = 18
            ),
            AdCreative(
                id = "ad_travel_4",
                title = "SkyFlight Vacations",
                headline = "Up to 60% Off Luxury Beach Resorts",
                description = "Unlock exclusive secret member hotel fares and free flight cancellations on your dream summer getaway.",
                callToAction = "Explore Deals",
                sponsorName = "SkyFlight Global Inc.",
                rating = 4.6f,
                downloads = "50M+",
                category = "Travel",
                primaryColorHex = 0xFF11998E,
                secondaryColorHex = 0xFF38EF7D,
                iconEmoji = "✈️",
                videoDurationSec = 15
            ),
            AdCreative(
                id = "ad_casual_5",
                title = "Royal Match Kingdom",
                headline = "Solve Puzzles & Build Castles!",
                description = "No Wi-Fi needed! Thousands of challenging match levels with royal rewards and exciting mini-games.",
                callToAction = "Play Now",
                sponsorName = "Royal Studios",
                rating = 4.9f,
                downloads = "100M+",
                category = "Casual",
                primaryColorHex = 0xFFF7971E,
                secondaryColorHex = 0xFFFFD200,
                iconEmoji = "👑",
                videoDurationSec = 15
            )
        )
    }
}


package com.example.ads
import com.example.BuildConfig

enum class AdType {
    BANNER,
    INTERSTITIAL,
    REWARDED,
    REWARDED_INTERSTITIAL,
    APP_OPEN
}

enum class RewardReason(val title: String, val rewardDescription: String) {
    EXTRA_LIVES("Full Refill", "+5 Extra Lives"),
    CONTINUE_LEVEL("Don't Give Up!", "+5 Extra Moves"),
    FREE_HAMMER("Lollipop Hammer", "1 Free Hammer Booster"),
    FREE_SWAP("Free Hand Swap", "1 Free Swap Booster"),
    COLOR_BOMB_START("Rainbow Bomb", "Start with Color Bomb on Board"),
    DAILY_BONUS_SPIN("Lucky Spin", "1 Free Extra Spin")
}

data class AdCreative(
    val id: String,
    val title: String,
    val headline: String,
    val description: String,
    val callToAction: String,
    val sponsorName: String,
    val rating: Float,
    val downloads: String,
    val category: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val iconEmoji: String,
    val videoDurationSec: Int = 15
)

data class AdMonetizationStats(
    val bannerImpressions: Int = 0,
    val interstitialImpressions: Int = 0,
    val rewardedImpressions: Int = 0,
    val appOpenImpressions: Int = 0,
    val estimatedEarningsUsd: Double = 0.0,
    val fillRate: Float = 99.8f
)



data class AdConfig(
    val bannerAdUnitId: String = BuildConfig.BANNER_AD_UNIT_ID.ifEmpty { "ca-app-pub-6762270587599212/2583002088" },
    val interstitialAdUnitId: String = BuildConfig.INTERSTITIAL_AD_UNIT_ID.ifEmpty { "ca-app-pub-6762270587599212/9667285182" },
    val rewardedAdUnitId: String = BuildConfig.REWARDED_AD_UNIT_ID.ifEmpty { "ca-app-pub-6762270587599212/3464461610" },
    val rewardedInterstitialAdUnitId: String = BuildConfig.REWARDED_INTERSTITIAL_AD_UNIT_ID.ifEmpty { "ca-app-pub-6762270587599212/6788628953" },
    val appOpenAdUnitId: String = BuildConfig.APP_OPEN_AD_UNIT_ID.ifEmpty { "ca-app-pub-6762270587599212/6306624673" },
    val testModeEnabled: Boolean = false,
    val bannerRefreshIntervalSec: Int = 20,
    val interstitialFrequencyLevels: Int = 2
)

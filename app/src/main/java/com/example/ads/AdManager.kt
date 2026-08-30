package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AdManager {
    private val _adStats = MutableStateFlow(AdMonetizationStats())
    val adStats: StateFlow<AdMonetizationStats> = _adStats.asStateFlow()

    private val _config = MutableStateFlow(AdConfig())
    val config: StateFlow<AdConfig> = _config.asStateFlow()

    // Stub for UI compatibility (no longer uses active simulated ads)
    val activeAd: StateFlow<Any?> = MutableStateFlow(null)
    val currentBannerAd: StateFlow<Any?> = MutableStateFlow(null)

    var currentActivity: Activity? = null
    private var isInitialized = false

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var appOpenAd: AppOpenAd? = null

    private var levelsSinceLastInterstitial = 0

    fun init(context: Context) {
        if (isInitialized) return
        MobileAds.initialize(context) {
            isInitialized = true
            loadInterstitial(context)
            loadRewarded(context)
            loadAppOpenAd(context)
        }
    }

    private fun loadInterstitial(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, _config.value.interstitialAdUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
            }
        })
    }

    private fun loadRewarded(context: Context) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, _config.value.rewardedAdUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                rewardedAd = null
            }
        })
    }

    private fun loadAppOpenAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(context, _config.value.appOpenAdUnitId, adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                appOpenAd = null
            }
        })
    }

    fun shouldShowInterstitial(): Boolean {
        levelsSinceLastInterstitial++
        return levelsSinceLastInterstitial >= _config.value.interstitialFrequencyLevels
    }

    fun showAppOpenAd(onDismiss: () -> Unit = {}) {
        val activity = currentActivity
        if (activity != null && appOpenAd != null) {
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    loadAppOpenAd(activity)
                    onDismiss()
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    appOpenAd = null
                    onDismiss()
                }
            }
            appOpenAd?.show(activity)
            _adStats.update { it.copy(appOpenImpressions = it.appOpenImpressions + 1) }
        } else {
            onDismiss()
        }
    }

    fun showInterstitial(onDismiss: () -> Unit) {
        levelsSinceLastInterstitial = 0
        val activity = currentActivity
        if (activity != null && interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial(activity)
                    onDismiss()
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    onDismiss()
                }
            }
            interstitialAd?.show(activity)
            _adStats.update { it.copy(interstitialImpressions = it.interstitialImpressions + 1) }
        } else {
            onDismiss()
        }
    }

    fun showRewardedAd(reason: RewardReason, onRewardGranted: (RewardReason) -> Unit, onDismiss: () -> Unit = {}) {
        val activity = currentActivity
        if (activity != null && rewardedAd != null) {
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewarded(activity)
                    onDismiss()
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    rewardedAd = null
                    onDismiss()
                }
            }
            rewardedAd?.show(activity) { rewardItem ->
                onRewardGranted(reason)
            }
            _adStats.update { it.copy(rewardedImpressions = it.rewardedImpressions + 1) }
        } else {
            onDismiss()
        }
    }

    fun showRewardedInterstitial(reason: RewardReason, onRewardGranted: (RewardReason) -> Unit, onDismiss: () -> Unit = {}) {
        showRewardedAd(reason, onRewardGranted, onDismiss)
    }

    fun closeActiveAd() {
        // No longer needed as AdMob handles UI closure internally
    }

    fun updateConfig(transform: (AdConfig) -> AdConfig) {
        _config.update(transform)
    }
}

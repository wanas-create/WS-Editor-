package com.example.monetization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MonetizationManager private constructor() {

    private val _isProUser = MutableStateFlow(false)
    val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()

    private val _is4kExportUnlocked = MutableStateFlow(false)
    val is4kExportUnlocked: StateFlow<Boolean> = _is4kExportUnlocked.asStateFlow()

    private val _isAiToolsUnlimited = MutableStateFlow(false)
    val isAiToolsUnlimited: StateFlow<Boolean> = _isAiToolsUnlimited.asStateFlow()

    private val _showInterstitialTrigger = MutableStateFlow(false)
    val showInterstitialTrigger: StateFlow<Boolean> = _showInterstitialTrigger.asStateFlow()

    private var lastAdShownTimestamp: Long = 0L

    fun upgradeToPro() {
        _isProUser.value = true
        _is4kExportUnlocked.value = true
        _isAiToolsUnlimited.value = true
    }

    fun downgradeToFree() {
        _isProUser.value = false
        _is4kExportUnlocked.value = false
        _isAiToolsUnlimited.value = false
    }

    /**
     * Trigger rewarded ad for 4K / 60FPS export unlock
     */
    fun watchRewardedAdFor4k(onRewardEarned: () -> Unit) {
        // In real AdMob integration, show AdMob RewardedAd
        _is4kExportUnlocked.value = true
        onRewardEarned()
    }

    /**
     * Safely request interstitial display outside of active editing session
     */
    fun triggerExportInterstitial(onComplete: () -> Unit) {
        if (_isProUser.value) {
            onComplete()
            return
        }
        val now = System.currentTimeMillis()
        if (now - lastAdShownTimestamp > AdMobConfig.AD_INTERVAL_MINUTES * 60 * 1000) {
            lastAdShownTimestamp = now
            _showInterstitialTrigger.value = true
        }
        onComplete()
    }

    fun dismissInterstitial() {
        _showInterstitialTrigger.value = false
    }

    companion object {
        val instance by lazy { MonetizationManager() }
    }
}

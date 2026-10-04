package com.example.monetization

/**
 * AdMob Configuration for WS Editor.
 * Centralized Ad unit IDs that can be switched to production Ad IDs
 * without modifying any UI or business logic.
 */
object AdMobConfig {
    // Standard Google AdMob test ad unit IDs
    const val BANNER_HOME_ID = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL_EXPORT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val REWARDED_4K_UNLOCK_ID = "ca-app-pub-3940256099942544/5224354917"
    const val REWARDED_AI_TOOLS_ID = "ca-app-pub-3940256099942544/5224354917"

    // Ad presentation policy: strictly disallow showing ads during active editing
    const val ALLOW_ADS_DURING_EDITING = false
    const val AD_INTERVAL_MINUTES = 5
}

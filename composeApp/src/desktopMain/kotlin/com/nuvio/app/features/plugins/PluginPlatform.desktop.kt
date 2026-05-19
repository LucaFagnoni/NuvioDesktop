package com.nuvio.app.features.plugins

import com.nuvio.app.core.storage.DesktopPrefsStorage

internal object PluginStorage {
    private const val pluginsStateKey = "plugins_state"

    fun loadState(profileId: Int): String? =
        DesktopPrefsStorage.getString("${pluginsStateKey}_$profileId")

    fun saveState(profileId: Int, payload: String) =
        DesktopPrefsStorage.putString("${pluginsStateKey}_$profileId", payload)
}

internal fun currentPluginPlatform(): String = "desktop"

internal fun currentEpochMillis(): Long = System.currentTimeMillis()

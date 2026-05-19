package com.nuvio.app.features.settings

import com.nuvio.app.core.storage.DesktopPrefsStorage
import com.nuvio.app.core.sync.decodeSyncBoolean
import com.nuvio.app.core.sync.decodeSyncString
import com.nuvio.app.core.sync.encodeSyncBoolean
import com.nuvio.app.core.sync.encodeSyncString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal actual object ThemeSettingsStorage {
    private const val selectedThemeKey = "theme_selected"
    private const val amoledEnabledKey = "theme_amoled"
    private const val liquidGlassNativeTabBarEnabledKey = "theme_liquid_glass"
    private const val selectedAppLanguageKey = "theme_app_language"
    private val syncKeys = listOf(
        selectedThemeKey, amoledEnabledKey, liquidGlassNativeTabBarEnabledKey, selectedAppLanguageKey,
    )

    actual fun loadSelectedTheme(): String? = DesktopPrefsStorage.getString(selectedThemeKey)
    actual fun saveSelectedTheme(themeName: String) = DesktopPrefsStorage.putString(selectedThemeKey, themeName)
    actual fun loadAmoledEnabled(): Boolean? = DesktopPrefsStorage.getBoolean(amoledEnabledKey)
    actual fun saveAmoledEnabled(enabled: Boolean) = DesktopPrefsStorage.putBoolean(amoledEnabledKey, enabled)
    actual fun loadLiquidGlassNativeTabBarEnabled(): Boolean? = DesktopPrefsStorage.getBoolean(liquidGlassNativeTabBarEnabledKey)
    actual fun saveLiquidGlassNativeTabBarEnabled(enabled: Boolean) = DesktopPrefsStorage.putBoolean(liquidGlassNativeTabBarEnabledKey, enabled)
    actual fun loadSelectedAppLanguage(): String? = DesktopPrefsStorage.getString(selectedAppLanguageKey)
    actual fun saveSelectedAppLanguage(languageCode: String) = DesktopPrefsStorage.putString(selectedAppLanguageKey, languageCode)
    actual fun applySelectedAppLanguage(languageCode: String) {
        java.util.Locale.setDefault(java.util.Locale.forLanguageTag(languageCode))
    }

    actual fun exportToSyncPayload(): JsonObject = buildJsonObject {
        loadSelectedTheme()?.let { put(selectedThemeKey, encodeSyncString(it)) }
        loadAmoledEnabled()?.let { put(amoledEnabledKey, encodeSyncBoolean(it)) }
        loadLiquidGlassNativeTabBarEnabled()?.let { put(liquidGlassNativeTabBarEnabledKey, encodeSyncBoolean(it)) }
        loadSelectedAppLanguage()?.let { put(selectedAppLanguageKey, encodeSyncString(it)) }
    }

    actual fun replaceFromSyncPayload(payload: JsonObject) {
        syncKeys.forEach { DesktopPrefsStorage.remove(it) }
        payload.decodeSyncString(selectedThemeKey)?.let(::saveSelectedTheme)
        payload.decodeSyncBoolean(amoledEnabledKey)?.let(::saveAmoledEnabled)
        payload.decodeSyncBoolean(liquidGlassNativeTabBarEnabledKey)?.let(::saveLiquidGlassNativeTabBarEnabled)
        payload.decodeSyncString(selectedAppLanguageKey)?.let(::saveSelectedAppLanguage)
    }
}

package com.nuvio.app.features.player

internal actual object DeviceLanguagePreferences {
    actual fun preferredLanguageCodes(): List<String> {
        val locale = java.util.Locale.getDefault()
        return listOf(locale.language)
    }
}

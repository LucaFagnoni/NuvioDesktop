package com.nuvio.app.features.details

import com.nuvio.app.core.storage.DesktopPrefsStorage

internal actual object MetaScreenSettingsStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("meta_screen_settings")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("meta_screen_settings", payload)
}

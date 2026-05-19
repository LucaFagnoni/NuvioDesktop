package com.nuvio.app.features.home

import com.nuvio.app.core.storage.DesktopPrefsStorage

internal actual object HomeCatalogSettingsStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("home_catalog_settings")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("home_catalog_settings", payload)
}

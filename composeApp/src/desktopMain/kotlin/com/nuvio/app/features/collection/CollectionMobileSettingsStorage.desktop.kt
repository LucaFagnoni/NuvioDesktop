package com.nuvio.app.features.collection

import com.nuvio.app.core.storage.DesktopPrefsStorage

internal actual object CollectionMobileSettingsStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("collection_mobile_settings")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("collection_mobile_settings", payload)
}

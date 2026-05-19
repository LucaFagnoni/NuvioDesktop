package com.nuvio.app.features.collection

import com.nuvio.app.core.storage.DesktopPrefsStorage

internal actual object CollectionStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("collection_payload")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("collection_payload", payload)
}

package com.nuvio.app.features.search

import com.nuvio.app.core.storage.DesktopPrefsStorage

internal actual object SearchHistoryStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("search_history")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("search_history", payload)
}

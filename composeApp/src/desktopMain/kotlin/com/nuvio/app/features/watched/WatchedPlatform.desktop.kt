package com.nuvio.app.features.watched

import com.nuvio.app.core.storage.DesktopPrefsStorage

actual object WatchedClock {
    actual fun nowEpochMs(): Long = System.currentTimeMillis()
}

actual object WatchedStorage {
    actual fun loadPayload(profileId: Int): String? = DesktopPrefsStorage.getString("watched_$profileId")
    actual fun savePayload(profileId: Int, payload: String) = DesktopPrefsStorage.putString("watched_$profileId", payload)
}

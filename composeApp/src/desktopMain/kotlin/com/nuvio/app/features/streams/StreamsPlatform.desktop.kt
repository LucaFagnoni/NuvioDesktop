package com.nuvio.app.features.streams

import com.nuvio.app.core.storage.DesktopPrefsStorage

internal actual fun epochMs(): Long = System.currentTimeMillis()

internal actual object StreamLinkCacheStorage {
    actual fun loadEntry(hashedKey: String): String? = DesktopPrefsStorage.getString("slc_$hashedKey")
    actual fun saveEntry(hashedKey: String, payload: String) = DesktopPrefsStorage.putString("slc_$hashedKey", payload)
    actual fun removeEntry(hashedKey: String) = DesktopPrefsStorage.remove("slc_$hashedKey")
}

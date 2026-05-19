package com.nuvio.app.features.library

import com.nuvio.app.core.storage.DesktopPrefsStorage

internal actual object LibraryClock {
    actual fun nowEpochMs(): Long = System.currentTimeMillis()
}

internal actual object LibraryStorage {
    actual fun loadPayload(profileId: Int): String? = DesktopPrefsStorage.getString("library_$profileId")
    actual fun savePayload(profileId: Int, payload: String) = DesktopPrefsStorage.putString("library_$profileId", payload)
}

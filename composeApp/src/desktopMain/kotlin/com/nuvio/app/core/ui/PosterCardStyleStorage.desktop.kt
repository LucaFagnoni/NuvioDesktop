package com.nuvio.app.core.ui

import com.nuvio.app.core.storage.DesktopPrefsStorage

internal actual object PosterCardStyleStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("poster_card_style")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("poster_card_style", payload)
}

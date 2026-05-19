package com.nuvio.app.core.auth

import com.nuvio.app.core.storage.DesktopPrefsStorage

internal actual object AuthStorage {
    actual fun loadAnonymousUserId(): String? = DesktopPrefsStorage.getString("auth_anonymous_user_id")
    actual fun saveAnonymousUserId(userId: String) = DesktopPrefsStorage.putString("auth_anonymous_user_id", userId)
    actual fun clearAnonymousUserId() = DesktopPrefsStorage.remove("auth_anonymous_user_id")
}

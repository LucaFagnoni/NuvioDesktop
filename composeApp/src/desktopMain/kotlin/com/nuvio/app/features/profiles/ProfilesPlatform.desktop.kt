package com.nuvio.app.features.profiles

import com.nuvio.app.core.storage.DesktopPrefsStorage
import java.security.MessageDigest

internal actual object AvatarStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("avatar_payload")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("avatar_payload", payload)
}

internal actual object ProfileHoverHapticFeedback {
    actual fun prepare() {}
    actual fun perform() {}
    actual fun release() {}
}

internal actual object ProfilePinCacheStorage {
    actual fun loadPayload(profileIndex: Int): String? = DesktopPrefsStorage.getString("pin_cache_$profileIndex")
    actual fun savePayload(profileIndex: Int, payload: String) = DesktopPrefsStorage.putString("pin_cache_$profileIndex", payload)
    actual fun removePayload(profileIndex: Int) = DesktopPrefsStorage.remove("pin_cache_$profileIndex")
}

internal actual object ProfilePinCrypto {
    actual fun sha256Hex(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(value.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}

internal actual object ProfileStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("profile_payload")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("profile_payload", payload)
}

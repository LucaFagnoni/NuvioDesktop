package com.nuvio.app.features.trakt

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.nuvio.app.core.storage.DesktopPrefsStorage
import com.nuvio.app.core.sync.decodeSyncBoolean
import com.nuvio.app.core.sync.encodeSyncBoolean
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.trakt_logo_wordmark
import nuvio.composeapp.generated.resources.trakt_tv_favicon
import org.jetbrains.compose.resources.painterResource
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal actual object TraktAuthStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("trakt_auth_payload")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("trakt_auth_payload", payload)
}

@Composable
actual fun traktBrandPainter(asset: TraktBrandAsset): Painter =
    painterResource(
        when (asset) {
            TraktBrandAsset.Glyph -> Res.drawable.trakt_tv_favicon
            TraktBrandAsset.Wordmark -> Res.drawable.trakt_logo_wordmark
        }
    )

internal actual object TraktCommentsStorage {
    private const val enabledKey = "trakt_comments_enabled"

    actual fun loadEnabled(): Boolean? = DesktopPrefsStorage.getBoolean(enabledKey)
    actual fun saveEnabled(enabled: Boolean) = DesktopPrefsStorage.putBoolean(enabledKey, enabled)

    actual fun exportToSyncPayload(): JsonObject = buildJsonObject {
        loadEnabled()?.let { put(enabledKey, encodeSyncBoolean(it)) }
    }

    actual fun replaceFromSyncPayload(payload: JsonObject) {
        DesktopPrefsStorage.remove(enabledKey)
        payload.decodeSyncBoolean(enabledKey)?.let(::saveEnabled)
    }
}

internal actual object TraktLibraryStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("trakt_library_payload")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("trakt_library_payload", payload)
}

internal actual object TraktPlatformClock {
    actual fun nowEpochMs(): Long = System.currentTimeMillis()
    actual fun parseIsoDateTimeToEpochMs(value: String): Long? {
        return runCatching {
            ZonedDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME).toInstant().toEpochMilli()
        }.getOrElse {
            runCatching {
                Instant.parse(value).toEpochMilli()
            }.getOrNull()
        }
    }
}

internal actual object TraktSettingsStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("trakt_settings_payload")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("trakt_settings_payload", payload)
}

package com.nuvio.app.features.mdblist

import com.nuvio.app.core.storage.DesktopPrefsStorage
import com.nuvio.app.core.sync.decodeSyncBoolean
import com.nuvio.app.core.sync.decodeSyncString
import com.nuvio.app.core.sync.encodeSyncBoolean
import com.nuvio.app.core.sync.encodeSyncString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal actual object MdbListSettingsStorage {
    private const val enabledKey = "mdblist_enabled"
    private const val apiKey = "mdblist_api_key"
    private const val useImdbKey = "mdblist_use_imdb"
    private const val useTmdbKey = "mdblist_use_tmdb"
    private const val useTomatoesKey = "mdblist_use_tomatoes"
    private const val useMetacriticKey = "mdblist_use_metacritic"
    private const val useTraktKey = "mdblist_use_trakt"
    private const val useLetterboxdKey = "mdblist_use_letterboxd"
    private const val useAudienceKey = "mdblist_use_audience"
    private val syncKeys = listOf(
        enabledKey, apiKey, useImdbKey, useTmdbKey, useTomatoesKey,
        useMetacriticKey, useTraktKey, useLetterboxdKey, useAudienceKey,
    )

    actual fun loadEnabled(): Boolean? = DesktopPrefsStorage.getBoolean(enabledKey)
    actual fun saveEnabled(enabled: Boolean) = DesktopPrefsStorage.putBoolean(enabledKey, enabled)
    actual fun loadApiKey(): String? = DesktopPrefsStorage.getString(apiKey)
    actual fun saveApiKey(apiKey: String) = DesktopPrefsStorage.putString(this.apiKey, apiKey)
    actual fun loadUseImdb(): Boolean? = DesktopPrefsStorage.getBoolean(useImdbKey)
    actual fun saveUseImdb(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useImdbKey, enabled)
    actual fun loadUseTmdb(): Boolean? = DesktopPrefsStorage.getBoolean(useTmdbKey)
    actual fun saveUseTmdb(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useTmdbKey, enabled)
    actual fun loadUseTomatoes(): Boolean? = DesktopPrefsStorage.getBoolean(useTomatoesKey)
    actual fun saveUseTomatoes(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useTomatoesKey, enabled)
    actual fun loadUseMetacritic(): Boolean? = DesktopPrefsStorage.getBoolean(useMetacriticKey)
    actual fun saveUseMetacritic(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useMetacriticKey, enabled)
    actual fun loadUseTrakt(): Boolean? = DesktopPrefsStorage.getBoolean(useTraktKey)
    actual fun saveUseTrakt(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useTraktKey, enabled)
    actual fun loadUseLetterboxd(): Boolean? = DesktopPrefsStorage.getBoolean(useLetterboxdKey)
    actual fun saveUseLetterboxd(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useLetterboxdKey, enabled)
    actual fun loadUseAudience(): Boolean? = DesktopPrefsStorage.getBoolean(useAudienceKey)
    actual fun saveUseAudience(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useAudienceKey, enabled)

    actual fun exportToSyncPayload(): JsonObject = buildJsonObject {
        loadEnabled()?.let { put(enabledKey, encodeSyncBoolean(it)) }
        loadApiKey()?.let { put(apiKey, encodeSyncString(it)) }
        loadUseImdb()?.let { put(useImdbKey, encodeSyncBoolean(it)) }
        loadUseTmdb()?.let { put(useTmdbKey, encodeSyncBoolean(it)) }
        loadUseTomatoes()?.let { put(useTomatoesKey, encodeSyncBoolean(it)) }
        loadUseMetacritic()?.let { put(useMetacriticKey, encodeSyncBoolean(it)) }
        loadUseTrakt()?.let { put(useTraktKey, encodeSyncBoolean(it)) }
        loadUseLetterboxd()?.let { put(useLetterboxdKey, encodeSyncBoolean(it)) }
        loadUseAudience()?.let { put(useAudienceKey, encodeSyncBoolean(it)) }
    }

    actual fun replaceFromSyncPayload(payload: JsonObject) {
        syncKeys.forEach { DesktopPrefsStorage.remove(it) }
        payload.decodeSyncBoolean(enabledKey)?.let(::saveEnabled)
        payload.decodeSyncString(apiKey)?.let(::saveApiKey)
        payload.decodeSyncBoolean(useImdbKey)?.let(::saveUseImdb)
        payload.decodeSyncBoolean(useTmdbKey)?.let(::saveUseTmdb)
        payload.decodeSyncBoolean(useTomatoesKey)?.let(::saveUseTomatoes)
        payload.decodeSyncBoolean(useMetacriticKey)?.let(::saveUseMetacritic)
        payload.decodeSyncBoolean(useTraktKey)?.let(::saveUseTrakt)
        payload.decodeSyncBoolean(useLetterboxdKey)?.let(::saveUseLetterboxd)
        payload.decodeSyncBoolean(useAudienceKey)?.let(::saveUseAudience)
    }
}

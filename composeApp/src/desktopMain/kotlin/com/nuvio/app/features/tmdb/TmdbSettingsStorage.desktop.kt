package com.nuvio.app.features.tmdb

import com.nuvio.app.core.storage.DesktopPrefsStorage
import com.nuvio.app.core.sync.decodeSyncBoolean
import com.nuvio.app.core.sync.decodeSyncString
import com.nuvio.app.core.sync.encodeSyncBoolean
import com.nuvio.app.core.sync.encodeSyncString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal actual object TmdbSettingsStorage {
    private const val enabledKey = "tmdb_enabled"
    private const val apiKeyKey = "tmdb_api_key"
    private const val languageKey = "tmdb_language"
    private const val useTrailersKey = "tmdb_use_trailers"
    private const val useArtworkKey = "tmdb_use_artwork"
    private const val useBasicInfoKey = "tmdb_use_basic_info"
    private const val useDetailsKey = "tmdb_use_details"
    private const val useCreditsKey = "tmdb_use_credits"
    private const val useProductionsKey = "tmdb_use_productions"
    private const val useNetworksKey = "tmdb_use_networks"
    private const val useEpisodesKey = "tmdb_use_episodes"
    private const val useSeasonPostersKey = "tmdb_use_season_posters"
    private const val useMoreLikeThisKey = "tmdb_use_more_like_this"
    private const val useCollectionsKey = "tmdb_use_collections"
    private val syncKeys = listOf(
        enabledKey, apiKeyKey, languageKey, useTrailersKey, useArtworkKey,
        useBasicInfoKey, useDetailsKey, useCreditsKey, useProductionsKey,
        useNetworksKey, useEpisodesKey, useSeasonPostersKey, useMoreLikeThisKey,
        useCollectionsKey,
    )

    actual fun loadEnabled(): Boolean? = DesktopPrefsStorage.getBoolean(enabledKey)
    actual fun saveEnabled(enabled: Boolean) = DesktopPrefsStorage.putBoolean(enabledKey, enabled)
    actual fun loadApiKey(): String? = DesktopPrefsStorage.getString(apiKeyKey)
    actual fun saveApiKey(apiKey: String) = DesktopPrefsStorage.putString(apiKeyKey, apiKey)
    actual fun loadLanguage(): String? = DesktopPrefsStorage.getString(languageKey)
    actual fun saveLanguage(language: String) = DesktopPrefsStorage.putString(languageKey, language)
    actual fun loadUseTrailers(): Boolean? = DesktopPrefsStorage.getBoolean(useTrailersKey)
    actual fun saveUseTrailers(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useTrailersKey, enabled)
    actual fun loadUseArtwork(): Boolean? = DesktopPrefsStorage.getBoolean(useArtworkKey)
    actual fun saveUseArtwork(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useArtworkKey, enabled)
    actual fun loadUseBasicInfo(): Boolean? = DesktopPrefsStorage.getBoolean(useBasicInfoKey)
    actual fun saveUseBasicInfo(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useBasicInfoKey, enabled)
    actual fun loadUseDetails(): Boolean? = DesktopPrefsStorage.getBoolean(useDetailsKey)
    actual fun saveUseDetails(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useDetailsKey, enabled)
    actual fun loadUseCredits(): Boolean? = DesktopPrefsStorage.getBoolean(useCreditsKey)
    actual fun saveUseCredits(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useCreditsKey, enabled)
    actual fun loadUseProductions(): Boolean? = DesktopPrefsStorage.getBoolean(useProductionsKey)
    actual fun saveUseProductions(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useProductionsKey, enabled)
    actual fun loadUseNetworks(): Boolean? = DesktopPrefsStorage.getBoolean(useNetworksKey)
    actual fun saveUseNetworks(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useNetworksKey, enabled)
    actual fun loadUseEpisodes(): Boolean? = DesktopPrefsStorage.getBoolean(useEpisodesKey)
    actual fun saveUseEpisodes(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useEpisodesKey, enabled)
    actual fun loadUseSeasonPosters(): Boolean? = DesktopPrefsStorage.getBoolean(useSeasonPostersKey)
    actual fun saveUseSeasonPosters(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useSeasonPostersKey, enabled)
    actual fun loadUseMoreLikeThis(): Boolean? = DesktopPrefsStorage.getBoolean(useMoreLikeThisKey)
    actual fun saveUseMoreLikeThis(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useMoreLikeThisKey, enabled)
    actual fun loadUseCollections(): Boolean? = DesktopPrefsStorage.getBoolean(useCollectionsKey)
    actual fun saveUseCollections(enabled: Boolean) = DesktopPrefsStorage.putBoolean(useCollectionsKey, enabled)

    actual fun exportToSyncPayload(): JsonObject = buildJsonObject {
        loadEnabled()?.let { put(enabledKey, encodeSyncBoolean(it)) }
        loadApiKey()?.let { put(apiKeyKey, encodeSyncString(it)) }
        loadLanguage()?.let { put(languageKey, encodeSyncString(it)) }
        loadUseTrailers()?.let { put(useTrailersKey, encodeSyncBoolean(it)) }
        loadUseArtwork()?.let { put(useArtworkKey, encodeSyncBoolean(it)) }
        loadUseBasicInfo()?.let { put(useBasicInfoKey, encodeSyncBoolean(it)) }
        loadUseDetails()?.let { put(useDetailsKey, encodeSyncBoolean(it)) }
        loadUseCredits()?.let { put(useCreditsKey, encodeSyncBoolean(it)) }
        loadUseProductions()?.let { put(useProductionsKey, encodeSyncBoolean(it)) }
        loadUseNetworks()?.let { put(useNetworksKey, encodeSyncBoolean(it)) }
        loadUseEpisodes()?.let { put(useEpisodesKey, encodeSyncBoolean(it)) }
        loadUseSeasonPosters()?.let { put(useSeasonPostersKey, encodeSyncBoolean(it)) }
        loadUseMoreLikeThis()?.let { put(useMoreLikeThisKey, encodeSyncBoolean(it)) }
        loadUseCollections()?.let { put(useCollectionsKey, encodeSyncBoolean(it)) }
    }

    actual fun replaceFromSyncPayload(payload: JsonObject) {
        syncKeys.forEach { DesktopPrefsStorage.remove(it) }
        payload.decodeSyncBoolean(enabledKey)?.let(::saveEnabled)
        payload.decodeSyncString(apiKeyKey)?.let(::saveApiKey)
        payload.decodeSyncString(languageKey)?.let(::saveLanguage)
        payload.decodeSyncBoolean(useTrailersKey)?.let(::saveUseTrailers)
        payload.decodeSyncBoolean(useArtworkKey)?.let(::saveUseArtwork)
        payload.decodeSyncBoolean(useBasicInfoKey)?.let(::saveUseBasicInfo)
        payload.decodeSyncBoolean(useDetailsKey)?.let(::saveUseDetails)
        payload.decodeSyncBoolean(useCreditsKey)?.let(::saveUseCredits)
        payload.decodeSyncBoolean(useProductionsKey)?.let(::saveUseProductions)
        payload.decodeSyncBoolean(useNetworksKey)?.let(::saveUseNetworks)
        payload.decodeSyncBoolean(useEpisodesKey)?.let(::saveUseEpisodes)
        payload.decodeSyncBoolean(useSeasonPostersKey)?.let(::saveUseSeasonPosters)
        payload.decodeSyncBoolean(useMoreLikeThisKey)?.let(::saveUseMoreLikeThis)
        payload.decodeSyncBoolean(useCollectionsKey)?.let(::saveUseCollections)
    }
}

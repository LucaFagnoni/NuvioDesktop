package com.nuvio.app.features.watchprogress

import com.nuvio.app.core.storage.DesktopPrefsStorage
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal actual object ContinueWatchingEnrichmentStorage {
    actual fun loadPayload(key: String): String? = DesktopPrefsStorage.getString("cwe_$key")
    actual fun savePayload(key: String, payload: String) = DesktopPrefsStorage.putString("cwe_$key", payload)
}

internal actual object ContinueWatchingPreferencesStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("cw_prefs")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("cw_prefs", payload)
}

actual object CurrentDateProvider {
    actual fun todayIsoDate(): String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
}

internal actual object ResumePromptStorage {
    actual fun loadWasInPlayer(): Boolean = DesktopPrefsStorage.getBoolean("resume_was_in_player") ?: false
    actual fun saveWasInPlayer(value: Boolean) = DesktopPrefsStorage.putBoolean("resume_was_in_player", value)
    actual fun loadLastPlayerVideoId(): String? = DesktopPrefsStorage.getString("resume_last_video_id")
    actual fun saveLastPlayerVideoId(videoId: String?) {
        if (videoId != null) DesktopPrefsStorage.putString("resume_last_video_id", videoId)
        else DesktopPrefsStorage.remove("resume_last_video_id")
    }
}

internal actual object WatchProgressClock {
    actual fun nowEpochMs(): Long = System.currentTimeMillis()
}

internal actual object WatchProgressStorage {
    actual fun loadPayload(profileId: Int): String? = DesktopPrefsStorage.getString("watch_progress_$profileId")
    actual fun savePayload(profileId: Int, payload: String) = DesktopPrefsStorage.putString("watch_progress_$profileId", payload)
}

package com.nuvio.app.features.details

import com.nuvio.app.core.storage.DesktopPrefsStorage

internal actual object SeasonViewModeStorage {
    actual fun load(): SeasonViewMode? = DesktopPrefsStorage.getString("season_view_mode")?.let { SeasonViewMode.parse(it) }
    actual fun save(mode: SeasonViewMode) = DesktopPrefsStorage.putString("season_view_mode", SeasonViewMode.persist(mode))
}

package com.nuvio.app.core.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal actual object AppForegroundMonitor {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    actual fun events(): Flow<Unit> = _events

    fun notifyForeground() {
        _events.tryEmit(Unit)
    }
}

package com.nuvio.app

class DesktopPlatform : Platform {
    override val name: String = "Windows Desktop"
}

actual fun getPlatform(): Platform = DesktopPlatform()

internal actual val isIos: Boolean = false

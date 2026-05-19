package com.nuvio.app

import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.Image

fun main() = application {
    val windowState = rememberWindowState(width = 1280.dp, height = 800.dp)
    val iconPainter = remember {
        val iconBytes = Thread.currentThread().contextClassLoader
            .getResourceAsStream("icon.png")
            ?.readBytes()
        iconBytes?.let {
            BitmapPainter(Image.makeFromEncoded(it).toComposeImageBitmap())
        }
    }
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Nuvio",
        icon = iconPainter,
    ) {
        App()
    }
}

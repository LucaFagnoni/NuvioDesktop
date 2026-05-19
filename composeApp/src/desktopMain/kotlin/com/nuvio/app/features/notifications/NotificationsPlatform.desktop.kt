package com.nuvio.app.features.notifications

import com.nuvio.app.core.storage.DesktopPrefsStorage
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.Toolkit
import java.awt.Image
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal actual object EpisodeReleaseNotificationPlatform {
    actual suspend fun notificationsAuthorized(): Boolean =
        SystemTray.isSupported()

    actual suspend fun requestAuthorization(): Boolean =
        SystemTray.isSupported()

    actual suspend fun scheduleEpisodeReleaseNotifications(requests: List<EpisodeReleaseNotificationRequest>) {
        // Store scheduled notifications in preferences for persistence
        // On desktop, we don't have a background scheduler, but we record them
        // and show them when the app is running.
        val ids = requests.map { it.requestId }
        DesktopPrefsStorage.putString("scheduled_notification_ids", ids.joinToString("\n"))
    }

    actual suspend fun clearScheduledEpisodeReleaseNotifications() {
        DesktopPrefsStorage.remove("scheduled_notification_ids")
    }

    actual suspend fun showTestNotification(request: EpisodeReleaseNotificationRequest) {
        if (!SystemTray.isSupported()) return
        try {
            val tray = SystemTray.getSystemTray()
            val image: Image = Toolkit.getDefaultToolkit().createImage(ByteArray(0))
            val trayIcon = TrayIcon(image, "Nuvio")
            trayIcon.isImageAutoSize = true
            tray.add(trayIcon)
            trayIcon.displayMessage(
                request.notificationTitle,
                request.notificationBody,
                TrayIcon.MessageType.INFO,
            )
            // Remove tray icon after a delay
            Thread {
                Thread.sleep(5000)
                tray.remove(trayIcon)
            }.apply { isDaemon = true }.start()
        } catch (_: Exception) {
            // SystemTray not available
        }
    }
}

internal actual object EpisodeReleaseNotificationsClock {
    actual fun isoDateFromEpochMs(epochMs: Long): String {
        return Instant.ofEpochMilli(epochMs)
            .atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}

internal actual object EpisodeReleaseNotificationsStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("episode_notifications_payload")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("episode_notifications_payload", payload)
}

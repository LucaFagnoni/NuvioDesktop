package com.nuvio.app.features.downloads

import com.nuvio.app.core.storage.DesktopPrefsStorage
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.net.Proxy
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

internal actual object DownloadsClock {
    actual fun nowEpochMs(): Long = System.currentTimeMillis()
}

internal actual object DownloadsLiveStatusPlatform {
    actual fun onItemsChanged(items: List<DownloadItem>) {
        // No live activity widget on Desktop
    }
}

private val downloadsDir: File by lazy {
    val userHome = System.getProperty("user.home")
    File(userHome, "Nuvio/Downloads").apply { mkdirs() }
}

private val httpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .followRedirects(true)
    .followSslRedirects(true)
    .proxy(Proxy.NO_PROXY)
    .build()

private val activeDownloads = ConcurrentHashMap<String, AtomicBoolean>()

internal actual object DownloadsPlatformDownloader {
    actual fun start(
        request: DownloadPlatformRequest,
        onProgress: (downloadedBytes: Long, totalBytes: Long?) -> Unit,
        onSuccess: (localFileUri: String, totalBytes: Long?) -> Unit,
        onFailure: (message: String) -> Unit,
    ): DownloadsTaskHandle {
        val cancelled = AtomicBoolean(false)
        val taskId = request.destinationFileName
        activeDownloads[taskId] = cancelled

        thread(name = "download-$taskId", isDaemon = true) {
            try {
                val httpRequestBuilder = Request.Builder().url(request.sourceUrl)
                request.sourceHeaders.forEach { (k, v) -> httpRequestBuilder.header(k, v) }

                val response = httpClient.newCall(httpRequestBuilder.build()).execute()
                if (!response.isSuccessful) {
                    onFailure("HTTP ${response.code}: ${response.message}")
                    return@thread
                }

                val body = response.body ?: run {
                    onFailure("Empty response body")
                    return@thread
                }

                val totalBytes = body.contentLength().takeIf { it > 0 }
                val destFile = File(downloadsDir, request.destinationFileName)
                val partFile = File(downloadsDir, "${request.destinationFileName}.part")

                body.byteStream().use { input ->
                    FileOutputStream(partFile).use { output ->
                        val buffer = ByteArray(64 * 1024)
                        var downloaded = 0L

                        while (true) {
                            if (cancelled.get()) {
                                partFile.delete()
                                return@thread
                            }
                            val read = input.read(buffer)
                            if (read <= 0) break
                            output.write(buffer, 0, read)
                            downloaded += read
                            onProgress(downloaded, totalBytes)
                        }
                    }
                }

                if (cancelled.get()) {
                    partFile.delete()
                    return@thread
                }

                partFile.renameTo(destFile)
                val localUri = destFile.toURI().toString()
                onSuccess(localUri, totalBytes)
            } catch (e: Exception) {
                if (!cancelled.get()) {
                    onFailure(e.message ?: "Download failed")
                }
            } finally {
                activeDownloads.remove(taskId)
            }
        }

        return object : DownloadsTaskHandle {
            override fun cancel() {
                cancelled.set(true)
            }
        }
    }

    actual fun removeFile(localFileUri: String?): Boolean {
        if (localFileUri == null) return false
        return try {
            val file = if (localFileUri.startsWith("file:")) {
                File(java.net.URI(localFileUri))
            } else {
                File(localFileUri)
            }
            file.delete()
        } catch (_: Exception) {
            false
        }
    }

    actual fun removePartialFile(destinationFileName: String): Boolean {
        val partFile = File(downloadsDir, "${destinationFileName}.part")
        return partFile.delete()
    }

    actual fun resolveLocalFileUri(localFileUri: String?, destinationFileName: String): String? {
        if (localFileUri != null) {
            val file = try {
                if (localFileUri.startsWith("file:")) File(java.net.URI(localFileUri)) else File(localFileUri)
            } catch (_: Exception) {
                null
            }
            if (file?.exists() == true) return localFileUri
        }
        val fallback = File(downloadsDir, destinationFileName)
        return if (fallback.exists()) fallback.toURI().toString() else null
    }
}

internal actual object DownloadsStorage {
    actual fun loadPayload(): String? = DesktopPrefsStorage.getString("downloads_payload")
    actual fun savePayload(payload: String) = DesktopPrefsStorage.putString("downloads_payload", payload)
}

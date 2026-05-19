package com.nuvio.app.features.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VolumeDown
import androidx.compose.material.icons.rounded.VolumeMute
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.nio.ByteBuffer

private val vlcAvailable: Boolean by lazy {
    try {
        NativeDiscovery().discover()
    } catch (_: Exception) {
        false
    }
}

@Composable
actual fun PlatformPlayerSurface(
    sourceUrl: String,
    sourceAudioUrl: String?,
    sourceHeaders: Map<String, String>,
    sourceResponseHeaders: Map<String, String>,
    useYoutubeChunkedPlayback: Boolean,
    modifier: Modifier,
    playWhenReady: Boolean,
    resizeMode: PlayerResizeMode,
    useNativeController: Boolean,
    onControllerReady: (PlayerEngineController) -> Unit,
    onSnapshot: (PlayerPlaybackSnapshot) -> Unit,
    onError: (String?) -> Unit,
) {
    if (!vlcAvailable) {
        Box(
            modifier = modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "VLC is not installed.\nPlease install VLC media player to enable video playback.",
                color = Color.White,
            )
        }
        LaunchedEffect(Unit) {
            onError("VLC media player is not installed.")
        }
        return
    }

    val coroutineScope = rememberCoroutineScope()
    val latestOnSnapshot = rememberUpdatedState(onSnapshot)
    val latestOnError = rememberUpdatedState(onError)

    var composeBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val renderCallback = remember {
        RenderCallback { _, nativeBuffers, bufferFormat ->
            val buffer = nativeBuffers[0]
            val w = bufferFormat.width
            val h = bufferFormat.height
            if (w <= 0 || h <= 0) return@RenderCallback

            try {
                buffer.rewind()
                val pixels = ByteArray(w * h * 4)
                buffer.get(pixels)

                val skiaBitmap = Bitmap()
                skiaBitmap.allocPixels(ImageInfo(w, h, ColorType.BGRA_8888, ColorAlphaType.PREMUL))
                skiaBitmap.installPixels(pixels)
                composeBitmap = skiaBitmap.asComposeImageBitmap()
            } catch (_: Exception) { }
        }
    }

    val bufferFormatCallback = remember {
        object : BufferFormatCallback {
            override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat {
                return RV32BufferFormat(sourceWidth, sourceHeight)
            }
            override fun allocatedBuffers(buffers: Array<out ByteBuffer>) {}
        }
    }

    val factory = remember { MediaPlayerFactory() }
    val mediaPlayer: EmbeddedMediaPlayer = remember {
        val player = factory.mediaPlayers().newEmbeddedMediaPlayer()
        val videoSurface = CallbackVideoSurface(
            bufferFormatCallback,
            renderCallback,
            true,
            VideoSurfaceAdapters.getVideoSurfaceAdapter(),
        )
        player.videoSurface().set(videoSurface)
        player
    }

    val mrlOptions = remember(sourceHeaders) {
        buildList {
            sourceHeaders.forEach { (key, value) ->
                add(":http-header=$key: $value")
            }
            // Lower caching for better play/pause responsiveness
            add(":network-caching=800")
            add(":live-caching=800")
            add(":file-caching=300")
        }.toTypedArray()
    }

    DisposableEffect(sourceUrl, sourceAudioUrl) {
        val listener = object : MediaPlayerEventAdapter() {
            override fun playing(mediaPlayer: MediaPlayer) {
                latestOnError.value(null)
                latestOnSnapshot.value(mediaPlayer.snapshot())
            }

            override fun paused(mediaPlayer: MediaPlayer) {
                latestOnSnapshot.value(mediaPlayer.snapshot())
            }

            override fun stopped(mediaPlayer: MediaPlayer) {
                latestOnSnapshot.value(mediaPlayer.snapshot())
            }

            override fun finished(mediaPlayer: MediaPlayer) {
                latestOnSnapshot.value(
                    PlayerPlaybackSnapshot(
                        isLoading = false, isPlaying = false, isEnded = true,
                        durationMs = mediaPlayer.status().length(),
                        positionMs = mediaPlayer.status().length(),
                        bufferedPositionMs = mediaPlayer.status().length(),
                        playbackSpeed = mediaPlayer.status().rate(),
                    )
                )
            }

            override fun error(mediaPlayer: MediaPlayer) {
                latestOnError.value("Playback error occurred")
                latestOnSnapshot.value(PlayerPlaybackSnapshot(isLoading = false, isPlaying = false))
            }

            override fun buffering(mediaPlayer: MediaPlayer, newCache: Float) {
                latestOnSnapshot.value(mediaPlayer.snapshot())
            }

            override fun lengthChanged(mediaPlayer: MediaPlayer, newLength: Long) {
                latestOnSnapshot.value(mediaPlayer.snapshot())
            }
        }

        mediaPlayer.events().addMediaPlayerEventListener(listener)

        if (!sourceAudioUrl.isNullOrBlank()) {
            mediaPlayer.media().play(sourceUrl, *mrlOptions, ":input-slave=$sourceAudioUrl")
        } else {
            mediaPlayer.media().play(sourceUrl, *mrlOptions)
        }

        if (!playWhenReady) {
            coroutineScope.launch {
                delay(500)
                mediaPlayer.controls().pause()
            }
        }

        onDispose {
            mediaPlayer.events().removeMediaPlayerEventListener(listener)
            mediaPlayer.controls().stop()
            mediaPlayer.release()
            factory.release()
        }
    }

    LaunchedEffect(playWhenReady) {
        if (mediaPlayer.status().isPlaying != playWhenReady) {
            if (playWhenReady) mediaPlayer.controls().play() else mediaPlayer.controls().pause()
        }
    }

    LaunchedEffect(mediaPlayer) {
        while (isActive) {
            latestOnSnapshot.value(mediaPlayer.snapshot())
            delay(250L)
        }
    }

    LaunchedEffect(mediaPlayer) {
        onControllerReady(VlcPlayerEngineController(mediaPlayer))
    }

    val bitmap = composeBitmap
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        if (bitmap != null) {
            val contentScale = when (resizeMode) {
                PlayerResizeMode.Fit -> ContentScale.Fit
                PlayerResizeMode.Fill -> ContentScale.Crop
                PlayerResizeMode.Zoom -> ContentScale.Crop
            }
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
            )
        }
    }
}

private class VlcPlayerEngineController(
    private val mp: EmbeddedMediaPlayer,
) : PlayerEngineController {
    override fun play() = mp.controls().play()
    override fun pause() = mp.controls().pause()
    override fun seekTo(positionMs: Long) = mp.controls().setTime(positionMs.coerceAtLeast(0L))
    override fun seekBy(offsetMs: Long) {
        mp.controls().setTime((mp.status().time() + offsetMs).coerceAtLeast(0L))
    }
    override fun retry() { mp.controls().stop(); mp.controls().play() }
    override fun setPlaybackSpeed(speed: Float) { mp.controls().setRate(speed) }

    override fun getAudioTracks(): List<AudioTrack> =
        mp.audio().trackDescriptions()
            .filter { it.id() != -1 }
            .mapIndexed { i, d ->
                AudioTrack(i, d.id().toString(), d.description() ?: "Track ${i + 1}",
                    null, mp.audio().track() == d.id())
            }

    override fun getSubtitleTracks(): List<SubtitleTrack> =
        mp.subpictures().trackDescriptions()
            .filter { it.id() != -1 }
            .mapIndexed { i, d ->
                SubtitleTrack(i, d.id().toString(), d.description() ?: "Track ${i + 1}",
                    null, mp.subpictures().track() == d.id(), false)
            }

    override fun selectAudioTrack(index: Int) {
        mp.audio().trackDescriptions().filter { it.id() != -1 }
            .getOrNull(index)?.let { mp.audio().setTrack(it.id()) }
    }

    override fun selectSubtitleTrack(index: Int) {
        if (index < 0) { mp.subpictures().setTrack(-1); return }
        mp.subpictures().trackDescriptions().filter { it.id() != -1 }
            .getOrNull(index)?.let { mp.subpictures().setTrack(it.id()) }
    }

    override fun setSubtitleUri(url: String) { mp.subpictures().setSubTitleFile(url) }
    override fun clearExternalSubtitle() { mp.subpictures().setTrack(-1) }
    override fun clearExternalSubtitleAndSelect(trackIndex: Int) {
        clearExternalSubtitle(); selectSubtitleTrack(trackIndex)
    }
    override fun applySubtitleStyle(style: SubtitleStyleState) {}

    // Volume control — desktop only
    override fun getVolume(): Int = mp.audio().volume().coerceIn(0, 200)
    override fun setVolume(percent: Int) { mp.audio().setVolume(percent.coerceIn(0, 200)) }
    override fun supportsVolume(): Boolean = true
}

private fun MediaPlayer.snapshot(): PlayerPlaybackSnapshot {
    val length = status().length().coerceAtLeast(0L)
    val time = status().time().coerceAtLeast(0L)
    return PlayerPlaybackSnapshot(
        isLoading = !status().isPlayable && !status().isPlaying,
        isPlaying = status().isPlaying, isEnded = false,
        durationMs = length, positionMs = time, bufferedPositionMs = length,
        playbackSpeed = status().rate(),
    )
}

@Composable actual fun LockPlayerToLandscape() {}
@Composable actual fun EnterImmersivePlayerMode(keepScreenAwake: Boolean) {}
@Composable actual fun ManagePlayerPictureInPicture(isPlaying: Boolean, playerSize: IntSize) {}
@Composable actual fun rememberPlayerGestureController(): PlayerGestureController? = null

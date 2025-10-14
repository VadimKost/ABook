package com.vako.abook.player

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.vako.domain.player.model.PlaybackState
import com.vako.domain.player.model.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AudioBookPlayer(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val _state: MutableStateFlow<PlaybackState> =
        MutableStateFlow(PlaybackState.Initializing)

    val state = _state.asStateFlow()
    private val sessionToken =
        SessionToken(context, ComponentName(context, PlaybackService::class.java))
    private var controllerFuture: ListenableFuture<MediaController>? = null
    var mediaController: MediaController? = null


    // TODO: Think about invokeOnCompletion
    init {
        startMediaControllerWatcher()
    }

    private fun startMediaControllerWatcher() {
        val job = coroutineScope.launch {
            _state.subscriptionCount.collect {
                if (it >= 1 && _state.value == PlaybackState.Initializing) {
                    mediaController = getMediaController()
                    _state.update { PlaybackState.Idle }
                    startUpdatingPlayerState()
                } else if (it == 0) {
                    releaseMediaController()
                }
            }
        }

        job.invokeOnCompletion { releaseMediaController() }
    }

    private fun releaseMediaController() {
        controllerFuture?.let { it -> MediaController.releaseFuture(it) }
        controllerFuture = null
        mediaController = null
    }

    private suspend fun getMediaController() = suspendCoroutine { continuation ->
        controllerFuture = MediaController.Builder(context, sessionToken).setListener(
            object : MediaController.Listener {
                override fun onExtrasChanged(controller: MediaController, extras: Bundle) {
                    _state.update {
                        if (it is PlaybackState.Ready) {
                            val sessionExtras = extras.fromBundle()
                            it.copy(timeToSleep = sessionExtras.timeToSleep)
                        } else it
                    }
                }
            }
        ).buildAsync()
        controllerFuture?.let {
            it.addListener({
                continuation.resume(it.get())
            }, MoreExecutors.directExecutor())
        }
    }

    fun setPlaylist(playlist: Playlist): Boolean {
        val currentState = _state.value

        return if (currentState is PlaybackState.Ready) {

            val isChanged = currentState.playlist != playlist
            if (isChanged) {
                _state.update { currentState.copy(playlist = playlist) }
                mediaController?.apply {
                    clearMediaItems()
                    addMediaItems(getMediaItems(playlist))
                }
            }
            isChanged
        } else {
            mediaController?.apply {
                addMediaItems(getMediaItems(playlist))
            }
            _state.update {
                PlaybackState.Ready(
                    timeToSleep = 0,
                    isPlaying = false,
                    playlist = playlist,
                    currentTrackIndex = 0,
                    currentPosition = 0L
                )
            }
            true
        }
    }

    fun startSleepTimer(time: Int) {
        // TODO: Improve
        val customCommand = StartSleepTimerCommand()
        val command = SessionCommand(
            customCommand.name,
            Bundle.EMPTY
        )
        mediaController?.sendCustomCommand(
            command,
            customCommand.toBundle(time)
        )
    }

    fun cancelSleepTimer() {
        val customCommand = CancelSleepTimerCommand()
        val command = SessionCommand(
            customCommand.name,
            Bundle.EMPTY
        )
        mediaController?.sendCustomCommand(
            command,
            Bundle.EMPTY
        )
    }

    private fun getMediaItems(playlist: Playlist): List<MediaItem> {
        return playlist.mediaItems.map { mediaItem ->
            MediaItem.Builder()
                .setUri(mediaItem.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setArtworkUri(playlist.cover.toUri())
                        .setTitle(mediaItem.title)
                        .setArtist(playlist.name)
                        .build()
                )
                .build()
        }
    }

    private fun startUpdatingPlayerState() {
        val player = mediaController
        requireNotNull(player)

        coroutineScope.launch {
            while (isActive) {
                val currentState = _state.value
                if (currentState is PlaybackState.Ready) {
                    _state.update {
                        currentState.copy(
                            isPlaying = player.playWhenReady,
                            currentTrackIndex = player.currentMediaItemIndex,
                            currentPosition = player.currentPosition
                        )
                    }
                }
                delay(500)
            }
        }
    }

    companion object {
        @Composable
        fun rememberServiceAudioBookPlayer(): AudioBookPlayer {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            return remember { AudioBookPlayer(context, scope) }
        }
    }
}
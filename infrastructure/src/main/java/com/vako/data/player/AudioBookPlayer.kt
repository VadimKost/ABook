package com.vako.data.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.vako.domain.player.PlayerGateway
import com.vako.domain.player.SleepTimer
import com.vako.domain.player.usecases.PlaybackCommand
import com.vako.domain.player.model.PlayerState
import com.vako.domain.player.model.Playlist
import com.vako.domain.player.model.SleepTimerState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioBookPlayer @Inject constructor(
    @param:ApplicationContext private val context: Context
) : PlayerGateway {
    private val _playerState: MutableStateFlow<PlayerState> =
        MutableStateFlow(PlayerState.Initializing)

    val playerState = _playerState.asStateFlow()
    private val controllerScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var updatePlayerStateJob: Job? = null
    private var releaseJob: Job? = null
    private var mediaController: MediaController? = null
    private var mediaControllerFuture: ListenableFuture<MediaController>? = null

    private var sleepTimer = SleepTimer(
        coroutineScope = controllerScope,
        onSleep = {
            mediaController?.pause()
        }
    )

    init {
        val serviceComponent =
            ComponentName("com.vako.abook", "com.vako.abook.player.PlaybackService")
        val sessionToken = SessionToken(context, serviceComponent)
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture?.addListener({
            mediaController = mediaControllerFuture?.get()
            updatePlayerState()
        }, MoreExecutors.directExecutor())
        // TODO: May replace by it
//        val intent = Intent().apply {
//            setComponent(serviceComponent)
//        }
//        ContextCompat.startForegroundService(context,intent)

        observeActiveState()
    }

    private fun updatePlayerState() {
        updatePlayerStateJob?.cancel()
        updatePlayerStateJob = controllerScope.launch {
            while (isActive) {
                val currentPlayerState = _playerState.value
                if (mediaController?.mediaItemCount == 0) {
                    _playerState.value = PlayerState.Idle
                } else if (currentPlayerState is PlayerState.Ready) {
                    mediaController?.let {
                        _playerState.value = currentPlayerState.copy(
                            isPlaying = it.playWhenReady,
                            currentTrackIndex = it.currentMediaItemIndex,
                            currentPosition = it.currentPosition,
                            sleepTimerState = sleepTimer.state.value
                        )
                    }
                }
                delay(500)
            }
        }
    }

    private fun observeActiveState() {
        controllerScope.launch {
            combine(
                _playerState.subscriptionCount.map { it > 0 },
                sleepTimer.state.map { it.isRunning }
            ) { hasPlayerObservers, timerRunning ->
                hasPlayerObservers || timerRunning
            }.distinctUntilChanged()
                .collect { isActive ->
                    if (isActive) cancelPendingRelease() else scheduleRelease()
                }
        }
    }

    private fun cancelPendingRelease() {
        releaseJob?.cancel()
    }

    private fun scheduleRelease() {
        releaseJob?.cancel()
        releaseJob = controllerScope.launch {
            delay(10_000) //release after 10 seconds of inactivity
            if (!isStillActive()) actuallyRelease()
        }
    }

    private fun actuallyRelease() {
        controllerScope.cancel()
        mediaControllerFuture?.let { MediaController.releaseFuture(it) }
    }

    private fun isStillActive(): Boolean {
        return _playerState.subscriptionCount.value > 0 ||
                sleepTimer.state.value.isRunning
    }

    override fun setPlaylist(playlist: Playlist): Boolean {
        val currentPlayerState = _playerState.value

        return if (currentPlayerState is PlayerState.Ready) {
            val isChanged = currentPlayerState.playlist != playlist
            if (isChanged) {
                _playerState.update { currentPlayerState.copy(playlist = playlist) }
                mediaController?.apply {
                    clearMediaItems()
                    addMediaItems(preparePlaylist(playlist))
                    prepare()
                }
            }
            isChanged
        } else {
            mediaController?.apply {
                addMediaItems(preparePlaylist(playlist))
                prepare()
            }
            _playerState.update {
                PlayerState.Ready(
                    isPlaying = false,
                    playlist = playlist,
                    currentTrackIndex = 0,
                    currentPosition = 0,
                    sleepTimerState = SleepTimerState(
                        isRunning = false,
                        timeRemainingSeconds = 0
                    )
                )
            }
            true
        }
    }

    private fun preparePlaylist(playlist: Playlist): List<MediaItem> {
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

    override fun observePlayerState(): Flow<PlayerState> = playerState

    override fun startSleepTimer(durationInSeconds: Int) {
        sleepTimer.setSleepTimer(durationInSeconds)
    }

    override fun cancelSleepTimer() {
        sleepTimer.cancelSleepTimer()
    }

    override fun play() {
        mediaController?.let { player ->
            player.prepare()
            player.play()
        }

    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun seekToNext() {
        mediaController?.seekToNext()
    }

    override fun seekToPrevious() {
        mediaController?.seekToPrevious()
    }

    override fun fastForward() {
        mediaController?.seekForward()
    }

    override fun rewind() {
        mediaController?.seekBack()
    }

    override fun seekTo(index: Int, time: Long) {
        mediaController?.seekTo(index, time)
    }
}
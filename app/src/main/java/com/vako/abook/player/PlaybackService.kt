package com.vako.abook.player

import android.content.Intent
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.vako.domain.player.PlayerGateway
import com.vako.domain.player.model.AudioBookVoiceoverPlaylist
import com.vako.domain.player.model.PlayerState
import com.vako.domain.user.usecases.SavePlaybackProgressUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var player: Player

    @Inject
    lateinit var playerGateway: PlayerGateway

    @Inject
    lateinit var savePlaybackProgressUseCase: SavePlaybackProgressUseCase
    var mediaSession: MediaSession? = null
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, player).build()
        /*addSession(mediaSession!!)*/
        coroutineScope.launch {
            observeIsPlaying().collect { playing ->
                if (!playing) {
                    savePaybackProgress()
                }
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        savePaybackProgress()
        coroutineScope.cancel()
        mediaSession?.run {
            player.release()
            release()

        }
        super.onDestroy()
    }

    fun savePaybackProgress() {
        try {
            val currentPlayerState = playerGateway.getCurrentPlayerState()
            if (currentPlayerState is PlayerState.Ready) {
                val currentPlaylist = currentPlayerState.playlist
                if (currentPlaylist is AudioBookVoiceoverPlaylist) {
                    val bookId = currentPlaylist.bookId
                    val voiceoverId = currentPlaylist.voiceoverId
                    val progress = currentPlayerState.playbackProgress
                    runBlocking {
                        savePlaybackProgressUseCase(bookId, voiceoverId, progress)
                    }
                }
            }
        } catch (_: Exception) {
            Log.d("PlaybackService", "Failed to save playback progress")
        }
    }

    fun observeIsPlaying() = flow {
        playerGateway.observePlayerState().collect {
            if (it is PlayerState.Ready) {
                emit(it.isPlaying)
            }
        }
    }.distinctUntilChanged()

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }
}
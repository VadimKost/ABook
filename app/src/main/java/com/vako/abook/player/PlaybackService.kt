package com.vako.abook.player

import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.vako.abook.player.features.SleepTimerController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var player: Player
    var mediaSession: MediaSession? = null

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val sleepTimerController = SleepTimerController(coroutineScope)

    override fun onCreate() {
        super.onCreate()
        val callback = CustomMediaSessionCallback(
            onStartSleepTimer = ::onStartSleepTimer,
            onCancelSleepTimer = ::onCancelSleepTimer
        )
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(callback)
            .build()

        startSendingNewMediaSessionExtras()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()

        }
        coroutineScope.cancel()
        super.onDestroy()
    }

    fun startSendingNewMediaSessionExtras() {
        coroutineScope.launch {
            sleepTimerController.timeToSleep.collect {
                val newSessionExtras = SessionExtras(it)
                mediaSession?.sessionExtras = newSessionExtras.toBundle()
            }
        }
    }

    fun onStartSleepTimer(duration: Int) {
        sleepTimerController.startTimer(
            durationSeconds = duration,
            onFinish = {
                mediaSession?.player?.pause()
            }
        )
    }

    fun onCancelSleepTimer() {
        sleepTimerController.cancelTimer()
    }
}
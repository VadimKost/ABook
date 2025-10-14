package com.vako.abook.player

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class CustomMediaSessionCallback(
    private val onStartSleepTimer: (Int) -> Unit,
    private val onCancelSleepTimer: () -> Unit
) : MediaSession.Callback {


    @OptIn(UnstableApi::class)
    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val sessionBuilder = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
        val sessionCommands = sessionBuilder.apply {
            CustomPlaybackServiceCommand.commands.forEach {
                add(SessionCommand(it.name, Bundle.EMPTY))
            }
        }.build()
        return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
            .setAvailableSessionCommands(sessionCommands)
            .build()
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        val command = customCommand.toCustomPlaybackServiceCommand()
        return when (command) {
            is CancelSleepTimerCommand -> {
                onCancelSleepTimer()
                Futures.immediateFuture(
                    SessionResult(SessionResult.RESULT_SUCCESS)
                )
            }

            is StartSleepTimerCommand -> {
                val timeToPlay = command.fromBundle(args)
                onStartSleepTimer(timeToPlay)
                Futures.immediateFuture(
                    SessionResult(SessionResult.RESULT_SUCCESS)
                )
            }

            null -> return super.onCustomCommand(session, controller, customCommand, args)
        }
    }
}

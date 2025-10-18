package com.vako.domain.player.usecases

import com.vako.domain.player.PlayerGateway
import com.vako.domain.player.model.Playlist
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandlePlaybackCommandUseCase @Inject constructor(
    val player: PlayerGateway
) {
    operator fun invoke(command: PlaybackCommand) {
        when (command) {
            PlaybackCommand.CancelSleepTimer -> player.cancelSleepTimer()
            PlaybackCommand.FastForward -> player.fastForward()
            PlaybackCommand.Next -> player.seekToNext()
            PlaybackCommand.Pause -> player.pause()
            PlaybackCommand.Play -> player.play()
            PlaybackCommand.Previous -> player.seekToPrevious()
            PlaybackCommand.Rewind -> player.rewind()
            is PlaybackCommand.SeekTo -> player.seekTo(command.index, command.time)
            is PlaybackCommand.SetPlaylist -> player.setPlaylist(command.playlist)
            is PlaybackCommand.SetSleepTimer -> player.startSleepTimer(command.durationInSeconds)
        }
    }
}

sealed class PlaybackCommand {
    data object Play : PlaybackCommand()
    data object Pause : PlaybackCommand()

    data object Next : PlaybackCommand()
    data object Previous : PlaybackCommand()

    data object Rewind : PlaybackCommand()
    data object FastForward : PlaybackCommand()

    data class SeekTo(val index: Int, val time: Long) : PlaybackCommand()

    data class SetPlaylist(val playlist: Playlist) : PlaybackCommand()

    data class SetSleepTimer(val durationInSeconds: Int) : PlaybackCommand()
    data object CancelSleepTimer : PlaybackCommand()
}
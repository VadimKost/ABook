package com.vako.domain.player.usecases

import com.vako.domain.book.usecases.GetBookByIdUseCase
import com.vako.domain.player.PlayerGateway
import com.vako.domain.player.model.AudioBookVoiceoverPlaylist
import com.vako.domain.shared.Resource
import com.vako.domain.user.usecases.RestorePlaybackProgressUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandlePlaybackCommandUseCase @Inject constructor(
    private val player: PlayerGateway,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val restorePlaybackProgressUseCase: RestorePlaybackProgressUseCase
) {
    suspend operator fun invoke(command: PlaybackCommand) {
        when (command) {
            PlaybackCommand.CancelSleepTimer -> player.cancelSleepTimer()
            PlaybackCommand.FastForward -> player.fastForward()
            PlaybackCommand.Next -> player.seekToNext()
            PlaybackCommand.Pause -> player.pause()
            PlaybackCommand.Play -> player.play()
            PlaybackCommand.Previous -> player.seekToPrevious()
            PlaybackCommand.Rewind -> player.rewind()
            is PlaybackCommand.SeekTo -> player.seekTo(command.index, command.time)
            is PlaybackCommand.StartBookVoiceoverPlayback ->
                onStartBookVoiceoverPlayback(command.bookId, command.voiceoverId)

            is PlaybackCommand.SetSleepTimer -> player.startSleepTimer(command.durationInSeconds)
        }
    }

    private suspend fun onStartBookVoiceoverPlayback(bookId: String, voiceoverId: String) {
        val book = getBookByIdUseCase(bookId)
        if (book is Resource.Success) {
            val voiceover = book.data.voiceovers.first { it.id == voiceoverId }
            val playlist = AudioBookVoiceoverPlaylist(
                name = book.data.title,
                cover = book.data.cover,
                mediaItems = voiceover.mediaItems,
                bookId = bookId,
                voiceoverId = voiceoverId
            )
            player.setPlaylist(playlist)
            val progress = restorePlaybackProgressUseCase(bookId, voiceoverId)
            if (progress != null) {
                player.seekTo(progress.trackIndex, progress.positionMs)
            }
            player.play()
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

    data class StartBookVoiceoverPlayback(
        val bookId: String,
        val voiceoverId: String
    ) : PlaybackCommand()

    data class SetSleepTimer(val durationInSeconds: Int) : PlaybackCommand()
    data object CancelSleepTimer : PlaybackCommand()
}
package com.vako.abook.presentation.screen.book

import com.vako.domain.book.model.Book
import com.vako.domain.book.model.Series
import com.vako.domain.book.model.Voiceover
import com.vako.domain.player.model.Playlist
import com.vako.domain.player.usecases.PlaybackCommand
import com.vako.domain.player.model.SleepTimerState

sealed interface BookEvent {
    data class VoiceoverSelected(val voiceover: Voiceover) : BookEvent
    data class HandlePlaybackCommand(val command: PlaybackCommand) : BookEvent
}

sealed interface BookAction

data class BookUiState(
    val isLoading: Boolean = true,
    val book: Book = Book(
        inAppId = "",
        title = "",
        cover = "",
        authors = emptyList(),
        voiceovers = emptyList(),
        series = Series(name = "", seriesIndex = -1)
    ),
    val selectedVoiceover: Voiceover? = null,
    val playbackState: VoiceoverPlaybackState = VoiceoverPlaybackState()
)

data class VoiceoverPlaybackState(
    val playlist: Playlist = Playlist(
        name = "",
        cover = "",
        mediaItems = listOf()
    ),
    val trackIndex: Int = 0,
    val positionMs: Long = 0,
    val isPlaying: Boolean = false,
    val sleepTimer: SleepTimerState = SleepTimerState(
        isRunning = false,
        timeRemainingSeconds = 0
    ),
    val isSelectedVoiceoverActive: Boolean = false
) {
    fun hasNext(): Boolean = trackIndex < playlist.mediaItems.lastIndex
    fun hasPrevious(): Boolean = trackIndex != 0
}

package com.vako.abook.presentation.screen.book

import com.vako.domain.book.model.Book
import com.vako.domain.book.model.Series
import com.vako.domain.book.model.Voiceover

sealed interface BookEvent {
    data class VoiceoverSelected(val voiceover: Voiceover) : BookEvent
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
)

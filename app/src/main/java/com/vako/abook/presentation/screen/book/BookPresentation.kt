package com.vako.abook.presentation.screen.book

import com.vako.domain.book.model.Book
import com.vako.domain.book.model.Series

sealed interface BookEvent {

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
)

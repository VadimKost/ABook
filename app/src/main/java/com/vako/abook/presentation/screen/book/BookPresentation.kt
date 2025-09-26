package com.vako.abook.presentation.screen.book

import com.vako.domain.book.model.Book

sealed interface BookEvent {

}

sealed interface BookAction

data class BookUiState(
    val isLoading: Boolean = true,
    val book: Book? = null,
)

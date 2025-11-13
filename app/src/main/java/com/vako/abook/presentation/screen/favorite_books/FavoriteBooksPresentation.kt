package com.vako.abook.presentation.screen.favorite_books

import com.vako.domain.book.model.Book


sealed interface FavoriteBookAction {
    data class OpenBook(val inAppBookId: String) : FavoriteBookAction
}

sealed interface FavoriteBookEvent {
    data object Retry : FavoriteBookEvent
}

data class FavoriteBooksUiState(
    val isLoading: Boolean = true,
    val books: List<Book> = listOf()
)
package com.vako.abook.presentation.screen.random_books

import com.vako.domain.book.model.Book

sealed interface RandomBooksEvent {
    object LoadMore : RandomBooksEvent
    data class Search(val query: String) : RandomBooksEvent
    data object SearchRequestHandled : RandomBooksEvent
}

sealed interface RandomBooksAction {
    data class OpenBook(val inAppBookId: String) : RandomBooksAction
}

data class RandomBooksUiState(
    val isLoading: Boolean = true,
    val randomBooks: List<Book> = emptyList(),
    val searchRequest: String? = null
)

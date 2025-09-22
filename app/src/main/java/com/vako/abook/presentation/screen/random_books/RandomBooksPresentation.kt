package com.vako.abook.presentation.screen.random_books

sealed interface RandomBooksIntent
sealed interface RandomBooksAction

data class RandomBooksUiState(
    val data: String = ""
)

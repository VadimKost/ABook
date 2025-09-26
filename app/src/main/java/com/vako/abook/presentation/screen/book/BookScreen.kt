package com.vako.abook.presentation.screen.book

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BookScreen(
    onAction: (BookAction) -> Unit
) {
    val viewModel: BookViewModel = hiltViewModel()
    RandomBooksContent()
}

@Composable
fun RandomBooksContent() {

}
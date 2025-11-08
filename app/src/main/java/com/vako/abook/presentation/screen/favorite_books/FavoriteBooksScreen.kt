package com.vako.abook.presentation.screen.favorite_books

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vako.abook.presentation.components.BookContainer
import com.vako.domain.book.model.Book

@Composable
fun FavoriteBooksScreen(
    onAction: (FavoriteBookAction) -> Unit
) {
    val viewModel: FavoriteBooksViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    FavoriteBooksContent(
        favoriteBooks = state.books,
        onBookClick = {
            onAction(FavoriteBookAction.OpenBook(it))
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteBooksContent(
    favoriteBooks: List<Book>,
    onBookClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Favorite Books")
                },
            )
        },
        content = { paddingValues ->
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                columns = GridCells.Adaptive(400.dp)
            ) {
                items(favoriteBooks) { book ->
                    BookContainer(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                        coverUrl = book.cover,
                        title = book.title,
                        authors = book.authors.map { it.fullName },
                        onBookClick = { onBookClick(book.inAppId) }
                    )
                }
            }
        }
    )
}
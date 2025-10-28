package com.vako.abook.presentation.screen.random_books

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vako.abook.presentation.components.BookContainer
import com.vako.domain.book.model.Author
import com.vako.domain.book.model.Book
import com.vako.domain.book.model.Reader
import com.vako.domain.book.model.Voiceover

@Composable
fun RandomBooksScreen(
    onAction: (RandomBooksAction) -> Unit
) {
    val viewModel: RandomBooksViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    val gridState = rememberLazyGridState()

    RandomBooksContent(
        randomBooks = state.randomBooks,
        gridState = gridState,
        isLoading = state.isLoading,
        onScrollToEnd = { viewModel.onEvent(RandomBooksEvent.LoadMore) },
        onBookClick = { onAction(RandomBooksAction.OpenBook(it)) },
        onSearchClick = { viewModel.onEvent(RandomBooksEvent.Search(it)) }
    )

    // TODO: Redo after search implementation
    LaunchedEffect(state.searchRequest) {
        val goto = state.searchRequest
        if (goto != null) {
            onAction(RandomBooksAction.OpenBook(goto))
            viewModel.onEvent(RandomBooksEvent.SearchRequestHandled)
        }
    }
}

fun Context.getLastClipboardText(): String? {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData: ClipData? = clipboard.primaryClip
    val item = clipData?.getItemAt(0)
    return item?.text?.toString()?.trim()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RandomBooksContent(
    randomBooks: List<Book>,
    gridState: LazyGridState,
    isLoading: Boolean,
    onScrollToEnd: () -> Unit,
    onBookClick: (String) -> Unit,
    onSearchClick: (String) -> Unit
) {
    LaunchedEffect(gridState, randomBooks.size) {
        snapshotFlow { gridState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index
                if (lastVisible == randomBooks.lastIndex && !isLoading) {
                    onScrollToEnd()
                }
            }
    }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Random Books")
                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                val s = context.getLastClipboardText()
                                s?.let {
                                    onSearchClick(it)
                                }
                            },
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                }
            )
        },
        content = { paddingValues ->
            LazyVerticalGrid(
                state = gridState,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                columns = GridCells.Adaptive(400.dp)
            ) {
                items(randomBooks) { book ->
                    BookContainer(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                        coverUrl = book.cover,
                        title = book.title,
                        authors = book.authors.map { it.fullName },
                        onBookClick = { onBookClick(book.inAppId) }
                    )
                }

                if (isLoading) {
                    item {
                        Box(
                            Modifier
                                .size(64.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    )
}


@Preview
@Composable
fun RandomBooksContentPreview() {
    val randomBooks = listOf(
        Book(
            inAppId = "1",
            title = "Book 1",
            cover = "cover1.jpg",
            authors = listOf(Author(fullName = "Author 1")),
            voiceovers = listOf(
                Voiceover(
                    id = "",
                    readers = listOf(Reader(fullName = "Reader 1")),
                    mediaItems = emptyList()
                )
            )
        ),
        Book(
            inAppId = "2",
            title = "Book 2",
            cover = "cover2.jpg",
            authors = listOf(Author(fullName = "Author 2")),
            voiceovers = listOf(
                Voiceover(
                    id = "",
                    readers = listOf(Reader(fullName = "Reader 2")),
                    mediaItems = emptyList()
                )
            )
        ),
    )
    RandomBooksContent(
        randomBooks = randomBooks,
        gridState = rememberLazyGridState(),
        isLoading = true,
        onScrollToEnd = {},
        onBookClick = {},
        onSearchClick = {}
    )
}




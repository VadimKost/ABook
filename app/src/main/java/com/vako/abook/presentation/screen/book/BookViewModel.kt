package com.vako.abook.presentation.screen.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.vako.abook.presentation.screen.random_books.RandomBooksUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    init {
        savedStateHandle.toRoute<BookRoute>().also {
            Log.e("BooksViewModel", "Route: $it")
        }
    }
    private val _state = MutableStateFlow(RandomBooksUiState())
    val state = _state.asStateFlow()
}
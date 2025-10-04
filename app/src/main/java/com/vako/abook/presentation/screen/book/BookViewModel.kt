package com.vako.abook.presentation.screen.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vako.domain.book.usecases.GetBookByIdUseCase
import com.vako.domain.shared.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getBookByIdUseCase: GetBookByIdUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BookUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val bookId = savedStateHandle.toRoute<BookRoute>().inAppId
            val result = getBookByIdUseCase(bookId)
            Log.e("BookViewModel", "init: $result" )
            if (result is Resource.Success) {
                _state.update {
                    it.copy(book = result.data, isLoading = false)
                }
            }
        }
    }
}
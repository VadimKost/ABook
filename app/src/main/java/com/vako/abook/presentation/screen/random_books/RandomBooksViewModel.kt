package com.vako.abook.presentation.screen.random_books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vako.domain.book.usecases.GetRandomBooksUseCase
import com.vako.domain.shared.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RandomBooksViewModel @Inject constructor(
    private val getRandomBooksUseCase: GetRandomBooksUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(RandomBooksUiState())
    val state = _state.asStateFlow()

    init {
        getRandomBooks()
    }

    fun onEvent(event: RandomBooksEvent) {
        when (event) {
            is RandomBooksEvent.LoadMore -> {
                getRandomBooks()
            }
        }
    }

    private fun getRandomBooks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            delay(1000)

            val randomBooks = getRandomBooksUseCase()
            if (randomBooks is Resource.Success) {
                _state.update { state ->
                    val books = state.randomBooks + randomBooks.data
                    state.copy(
                        randomBooks = books.distinctBy { it.inAppId },
                        isLoading = false,
                    )
                }
            }
        }
    }
}
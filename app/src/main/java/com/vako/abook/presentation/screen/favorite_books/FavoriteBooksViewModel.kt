package com.vako.abook.presentation.screen.favorite_books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vako.domain.book.model.Book
import com.vako.domain.shared.Resource
import com.vako.domain.user.usecases.ObserveFavoriteBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FavoriteBooksViewModel @Inject constructor(
    private val observeFavoriteBooksUseCase: ObserveFavoriteBooksUseCase
) : ViewModel() {

    val state: StateFlow<FavoriteBooksUiState> = observeFavoriteBooksUseCase().map { favoriteBooks ->
        assembleState(favoriteBooks)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoriteBooksUiState(),
    )

    private fun assembleState(favoriteBooksState: Resource<List<Book>>) : FavoriteBooksUiState {
        return when (favoriteBooksState) {
            is Resource.Error -> {
                TODO()
            }

            is Resource.Pending -> {
                FavoriteBooksUiState(
                    isLoading = true,
                    books = listOf()
                )
            }

            is Resource.Success -> {
                FavoriteBooksUiState(
                    isLoading = false,
                    books = favoriteBooksState.data
                )
            }
        }
    }

    fun onEvent(event: FavoriteBookEvent) {
        when (event) {
            else -> TODO()
        }
    }

}
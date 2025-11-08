package com.vako.abook.presentation.screen.favorite_books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vako.domain.shared.Resource
import com.vako.domain.user.usecases.GetFavoriteBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteBooksViewModel @Inject constructor(
    private val getFavoriteBooksUseCase: GetFavoriteBooksUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(FavoriteBooksUiState())
    val state = _state.asStateFlow()

    init {
        loadFavoriteBooks()
    }

    private fun loadFavoriteBooks() {
        viewModelScope.launch {
            val favoriteBooks = getFavoriteBooksUseCase()
            if (favoriteBooks is Resource.Success){
                _state.update {
                    it.copy(
                        isLoading = false,
                        books = favoriteBooks.data
                    )
                }
            }
        }
    }
}
package com.vako.abook.presentation.screen.random_books

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vako.domain.usecases.GetRandomBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@HiltViewModel
class RandomBooksViewModel @Inject constructor(
    private val getRandomBooksUseCase: GetRandomBooksUseCase
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getRandomBooksUseCase().also {
                Log.e("a", "RandomBooksViewModel init: $it" )
            }
        }
    }
    private val _state = MutableStateFlow(RandomBooksUiState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: RandomBooksIntent) {
        when (intent) {
            else -> Unit
        }
    }
}
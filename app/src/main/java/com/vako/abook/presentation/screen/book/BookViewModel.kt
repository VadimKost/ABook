package com.vako.abook.presentation.screen.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vako.domain.book.model.Voiceover
import com.vako.domain.book.usecases.GetBookByIdUseCase
import com.vako.domain.player.model.PlayerState
import com.vako.domain.player.usecases.HandlePlaybackCommandUseCase
import com.vako.domain.player.usecases.ObservePlayerStateUseCase
import com.vako.domain.player.usecases.PlaybackCommand
import com.vako.domain.shared.Resource
import com.vako.domain.user.usecases.GetPreferredVoiceoverIdForBookUseCase
import com.vako.domain.user.usecases.ObserveBookIsFavoriteUseCase
import com.vako.domain.user.usecases.SavePreferredVoiceoverForBookUseCase
import com.vako.domain.user.usecases.ToggleIsBookToFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val observePlayerStateUseCase: ObservePlayerStateUseCase,
    private val handlePlaybackCommandUseCase: HandlePlaybackCommandUseCase,
    private val toggleIsBookToFavoriteUseCase: ToggleIsBookToFavoriteUseCase,
    private val observeBookIsFavoriteUseCase: ObserveBookIsFavoriteUseCase,
    private val savePreferredVoiceoverForBookUseCase: SavePreferredVoiceoverForBookUseCase,
    private val getPreferredVoiceoverIdForBookUseCase: GetPreferredVoiceoverIdForBookUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(BookUiState())
    val state = _state.asStateFlow()

    val isSelectedVoiceoverActive = flow {
        observePlayerStateUseCase().collect { playerState ->
            if (playerState is PlayerState.Ready) {
                val isActive =
                    playerState.playlist.mediaItems == state.value.selectedVoiceover?.mediaItems
                emit(isActive)
            } else {
                emit(false)
            }
        }
    }

    init {
        loadBook()
        updatePlaybackState()
        observeIsBookFavorite()
    }

    fun onEvent(event: BookEvent) {
        when (event) {
            is BookEvent.VoiceoverSelected -> onVoiceoverSelected(event.voiceover)
            is BookEvent.HandlePlaybackCommand -> onHandlePlaybackCommand(event.command)
            is BookEvent.ShowVoiceoverSelectionDialog -> onShowVoiceoverSelectionDialog(event.show)
            is BookEvent.ShowSleepTimerDialog -> onShowSleepTimerDialog(event.show)
            is BookEvent.ToggleIsBookToFavorite -> onToggleIsBookToFavorite()
        }
    }

    fun observeIsBookFavorite() {
        viewModelScope.launch {
            val bookId = savedStateHandle.toRoute<BookRoute>().inAppId
            observeBookIsFavoriteUseCase(bookId).collect { isFavorite ->
                _state.update {
                    it.copy(isFavoriteBook = isFavorite)
                }
            }
        }
    }

    fun onToggleIsBookToFavorite() {
        viewModelScope.launch {
            toggleIsBookToFavoriteUseCase(state.value.book.inAppId)
        }
    }

    fun onShowSleepTimerDialog(show: Boolean) {
        _state.update {
            it.copy(
                showSleepTimerDialog = show
            )
        }
    }

    fun onShowVoiceoverSelectionDialog(show: Boolean) {
        _state.update {
            it.copy(
                showVoiceoverSelectionDialog = show
            )
        }
    }

    fun onHandlePlaybackCommand(command: PlaybackCommand) {
        viewModelScope.launch {
            handlePlaybackCommandUseCase(command)
        }
    }

    fun onVoiceoverSelected(voiceover: Voiceover) {
        viewModelScope.launch {
            savePreferredVoiceoverForBookUseCase(
                bookId = state.value.book.inAppId,
                voiceoverId = voiceover.id
            )
            _state.update {
                it.copy(
                    selectedVoiceover = voiceover
                )
            }
        }
    }

    fun updatePlaybackState() {
        viewModelScope.launch {
            combine(
                observePlayerStateUseCase(),
                isSelectedVoiceoverActive
            ) { playerState, isSelectedVoiceoverActive ->
                if (playerState is PlayerState.Ready) {
                    VoiceoverPlaybackState(
                        playlist = playerState.playlist,
                        trackIndex = playerState.playbackProgress.trackIndex,
                        positionMs = playerState.playbackProgress.positionMs,
                        isPlaying = playerState.isPlaying,
                        sleepTimer = playerState.sleepTimerState,
                        isSelectedVoiceoverActive = isSelectedVoiceoverActive
                    )
                } else {
                    VoiceoverPlaybackState()
                }
            }.collect { playbackState ->
                _state.update {
                    it.copy(
                        playbackState = playbackState
                    )
                }
            }
        }
    }

    fun loadBook() {
        viewModelScope.launch {
            val bookId = savedStateHandle.toRoute<BookRoute>().inAppId
            val result = getBookByIdUseCase(bookId)
            if (result is Resource.Success) {
                val voiceovers = result.data.voiceovers
                val preferredVoiceoverId =
                    getPreferredVoiceoverIdForBookUseCase(bookId)
                Log.e("asd book preferredVoiceoverId", preferredVoiceoverId.toString())
                val selectedVoiceover =
                    voiceovers.find { it.id == preferredVoiceoverId } ?: voiceovers.first()
                Log.e("asd book by id", result.data.toString())
                Log.e("asd book by id voice", voiceovers.size.toString())
                Log.e("asd book by id voice", voiceovers.toString())
                /*                val selectedVoiceover =
                                    if (voiceovers.size == 1) result.data.voiceovers.first() else null*/
                _state.update {
                    it.copy(
                        selectedVoiceover = selectedVoiceover,
                        book = result.data,
                        isLoading = false
                    )
                }
            }
        }
    }

}
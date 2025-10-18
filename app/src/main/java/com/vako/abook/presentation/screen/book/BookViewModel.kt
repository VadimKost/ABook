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
    private val handlePlaybackCommandUseCase: HandlePlaybackCommandUseCase
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
    }

    fun onEvent(event: BookEvent) {
        when (event) {
            is BookEvent.VoiceoverSelected -> onVoiceoverSelected(event.voiceover)
            is BookEvent.HandlePlaybackCommand -> handlePlaybackCommand(event.command)
        }
    }

    fun handlePlaybackCommand(command: PlaybackCommand) {
        handlePlaybackCommandUseCase(command)
    }

    fun onVoiceoverSelected(voiceover: Voiceover) {
        _state.update {
            it.copy(
                selectedVoiceover = voiceover
            )
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
                        trackIndex = playerState.currentTrackIndex,
                        positionMs = playerState.currentPosition,
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
                val voiceovers = result.data.voiceovers.filter { it.mediaItems.isNotEmpty() }
                val selectedVoiceover = voiceovers.first()
                Log.e("asd",result.data.toString())
                Log.e("asd",voiceovers.size.toString())
                Log.e("asd",voiceovers.toString())
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
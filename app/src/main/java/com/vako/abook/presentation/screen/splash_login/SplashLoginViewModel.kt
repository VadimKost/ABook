package com.vako.abook.presentation.screen.splash_login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vako.domain.user.usecases.auth.IsUserLoggedInUseCase
import com.vako.domain.user.usecases.auth.SignInAnonymouslyUseCase
import com.vako.domain.user.usecases.auth.SignInViaGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashLoginViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val signInViaGoogleUseCase: SignInViaGoogleUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SplashLoginUiState())
    val state = _state.asStateFlow()

    init {
        startApp()
    }

    fun onEvent(event: SplashLoginEvent) {
        when (event) {
            SplashLoginEvent.ContinueAsGuest -> signInAnonymously()
            is SplashLoginEvent.SignInViaGoogle -> signInViaGoogle(event.token)
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            signInAnonymouslyUseCase()
            _state.update {
                it.copy(isLogged = true)
            }
        }
    }

    fun signInViaGoogle(token: String) {
        viewModelScope.launch {
            signInViaGoogleUseCase(token)
            _state.update {
                it.copy(isLogged = true)
            }
        }
    }

    fun startApp() {
        viewModelScope.launch {
            val showingLogoJob = viewModelScope.launch {
                delay(2000)
            }

            val checkingUserJob = viewModelScope.launch {
                val isLoggedIn = isUserLoggedInUseCase()
                _state.update {
                    it.copy(
                        isLogged = isLoggedIn
                    )
                }
            }
            showingLogoJob.join()
            checkingUserJob.join()
            _state.update {
                it.copy(isShowingLogo = false)
            }
        }
    }

}
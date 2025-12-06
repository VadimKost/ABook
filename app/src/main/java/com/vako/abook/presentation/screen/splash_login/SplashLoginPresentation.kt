package com.vako.abook.presentation.screen.splash_login

sealed interface SplashLoginEvent {
    data class SignInViaGoogle(val token: String) : SplashLoginEvent
    data object ContinueAsGuest : SplashLoginEvent
}

sealed interface SplashLoginAction {
    data object NavigateHome : SplashLoginAction
}

data class SplashLoginUiState(
    val isShowingLogo: Boolean = true,
    val isLogged: Boolean = false
)
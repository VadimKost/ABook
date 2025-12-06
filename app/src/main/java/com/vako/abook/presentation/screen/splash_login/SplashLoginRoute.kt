package com.vako.abook.presentation.screen.splash_login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vako.abook.presentation.navigation.Screen
import com.vako.abook.presentation.navigation.performNavigationAction
import kotlinx.serialization.Serializable

@Serializable
data object SplashLoginRoute : Screen

internal fun NavController.navigateToSplashLogin() = navigate(route = SplashLoginRoute)

internal fun NavGraphBuilder.splashLogin(
    onNavigateHome: () -> Unit
) {
    composable<SplashLoginRoute> { backStackEntry ->
        SplashLoginScreen(
            onAction = {
                backStackEntry.performNavigationAction {
                    when(it){
                        SplashLoginAction.NavigateHome -> onNavigateHome()
                    }
                }
            }
        )
    }
}
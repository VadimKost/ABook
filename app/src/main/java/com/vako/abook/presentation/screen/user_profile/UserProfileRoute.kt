package com.vako.abook.presentation.screen.user_profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vako.abook.presentation.navigation.Screen
import com.vako.abook.presentation.navigation.performNavigationAction
import kotlinx.serialization.Serializable

@Serializable
data object UserProfileRoute : Screen

internal fun NavController.navigateToUserProfile() = navigate(route = UserProfileRoute)

internal fun NavGraphBuilder.userProfile() {
    composable<UserProfileRoute> { backStackEntry ->
        UserProfileScreen(
            onAction = {
                backStackEntry.performNavigationAction {
                    when (it) {
                        else -> Unit
                    }
                }
            }
        )
    }
}
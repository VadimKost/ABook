package com.vako.abook.presentation.screen.book

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vako.abook.presentation.navigation.Screen
import com.vako.abook.presentation.navigation.performNavigationAction
import kotlinx.serialization.Serializable

@Serializable
data class BookRoute(val inAppId: String) : Screen

internal fun NavController.navigateToBook(inAppId: String) = navigate(route = BookRoute(inAppId))

internal fun NavGraphBuilder.book() {
    composable<BookRoute> { backStackEntry ->
        BookScreen(
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
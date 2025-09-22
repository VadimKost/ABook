package com.vako.abook.presentation.screen.random_books

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vako.abook.presentation.navigation.Screen
import com.vako.abook.presentation.navigation.performNavigationAction
import kotlinx.serialization.Serializable

@Serializable
data object RandomBooksRoute : Screen

internal fun NavController.navigateToRandomBooks() = navigate(route = RandomBooksRoute)

internal fun NavGraphBuilder.randomBook() {
    composable<RandomBooksRoute> { backStackEntry ->
        RandomBooksScreen(
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
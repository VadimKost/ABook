package com.vako.abook.presentation.screen.favorite_books

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vako.abook.presentation.navigation.Screen
import com.vako.abook.presentation.navigation.performNavigationAction
import com.vako.abook.presentation.screen.book.BookRoute
import kotlinx.serialization.Serializable

@Serializable
data object FavoriteBooksRoute : Screen

internal fun NavController.navigateToFavoriteBooks() = navigate(route = FavoriteBooksRoute)

internal fun NavGraphBuilder.favoriteBooks(
    onNavigateToBook: (String) -> Unit
) {
    composable<FavoriteBooksRoute> { backStackEntry ->
        FavoriteBooksScreen(
            onAction = {
                backStackEntry.performNavigationAction {
                    when (it) {
                        is FavoriteBookAction.OpenBook -> onNavigateToBook(it.inAppBookId)
                    }
                }
            }
        )
    }
}
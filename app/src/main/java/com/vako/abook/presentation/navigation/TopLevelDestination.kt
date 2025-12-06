package com.vako.abook.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.vako.abook.R
import com.vako.abook.presentation.screen.favorite_books.FavoriteBooksRoute
import com.vako.abook.presentation.screen.random_books.RandomBooksRoute
import com.vako.abook.presentation.screen.user_profile.UserProfileRoute

enum class TopLevelDestination(
    @param:StringRes val label: Int,
    val icon: ImageVector,
    val route: Screen,
) {
    RandomBooks(
        label = R.string.random_books,
        icon = Icons.Outlined.Casino,
        route = RandomBooksRoute,
    ),

    FavoriteBooks(
        label = R.string.favorite_books,
        icon = Icons.Outlined.Favorite,
        route = FavoriteBooksRoute,
    ),

    UserProfile(
        label = R.string.profile,
        icon = Icons.Outlined.Person,
        route = UserProfileRoute
    )
}

interface Screen
package com.vako.abook.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.ui.graphics.vector.ImageVector
import com.vako.abook.R
import com.vako.abook.presentation.screen.random_books.RandomBooksRoute

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
}

interface Screen
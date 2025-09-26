package com.vako.abook.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vako.abook.presentation.screen.book.book
import com.vako.abook.presentation.screen.book.navigateToBook
import com.vako.abook.presentation.screen.random_books.RandomBooksRoute
import com.vako.abook.presentation.screen.random_books.randomBook

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(destination.label)) },
                    selected = currentDestination?.hierarchy
                        ?.any { it.hasRoute(destination.route::class) } == true,
                    onClick = {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        }

    ) {
        NavHost(navController = navController, startDestination = RandomBooksRoute) {
            randomBook(
                onNavigateToBook = { bookId ->
                    navController.navigateToBook(bookId)
                }
            )

            book()
        }
    }
}

inline fun NavBackStackEntry.performNavigationAction(body: () -> Unit) {
    if (this.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
        body()
    }
}

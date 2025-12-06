package com.vako.abook.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
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
import androidx.window.core.layout.WindowWidthSizeClass
import com.vako.abook.presentation.screen.book.book
import com.vako.abook.presentation.screen.book.navigateToBook
import com.vako.abook.presentation.screen.favorite_books.favoriteBooks
import com.vako.abook.presentation.screen.random_books.navigateToRandomBooks
import com.vako.abook.presentation.screen.random_books.randomBook
import com.vako.abook.presentation.screen.splash_login.SplashLoginRoute
import com.vako.abook.presentation.screen.splash_login.splashLogin
import com.vako.abook.presentation.screen.user_profile.userProfile

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isNavigationVisible = currentDestination?.hasRoute(SplashLoginRoute::class) != true
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val customNavSuiteType =
        with(adaptiveInfo) {
            if (!isNavigationVisible){
                NavigationSuiteType.None
            } else if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
                NavigationSuiteType.NavigationDrawer
            } else {
                NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
            }
        }

    NavigationSuiteScaffold(
        layoutType = customNavSuiteType,
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
        NavHost(navController = navController, startDestination = SplashLoginRoute) {
            randomBook(
                onNavigateToBook = { bookId ->
                    navController.navigateToBook(bookId)
                }
            )
            book()
            favoriteBooks(
                onNavigateToBook = { bookId ->
                    navController.navigateToBook(bookId)
                }
            )
            userProfile()
            splashLogin(
                onNavigateHome = {
                    navController.navigateToRandomBooks()
                }
            )
        }
    }
}

inline fun NavBackStackEntry.performNavigationAction(body: () -> Unit) {
    if (this.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
        body()
    }
}

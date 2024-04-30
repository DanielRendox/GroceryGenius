package com.rendox.grocerygenius.feature.dashboard_screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.rendox.grocerygenius.feature.grocery_list.GROCERY_LIST_ROUTE
import com.rendox.grocerygenius.ui.GroceryGeniusTransition

const val GROCERY_LISTS_DASHBOARD_ROUTE = "grocery_lists_dashboard_route"

fun NavController.navigateToGroceryListsDashboard(
    navOptions: (NavOptionsBuilder.() -> Unit) = {},
) {
    this.navigate(
        route = GROCERY_LISTS_DASHBOARD_ROUTE,
        builder = navOptions,
    )
}

fun NavGraphBuilder.groceryListsDashboardScreen(
    navigateToGroceryListScreen: (String) -> Unit,
    navigateToSettingsScreen: () -> Unit,
) {
    composable(
        route = GROCERY_LISTS_DASHBOARD_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                GROCERY_LIST_ROUTE -> GroceryGeniusTransition.SlideInHorizontallyEnterBackward
                else -> EnterTransition.None
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                GROCERY_LIST_ROUTE -> GroceryGeniusTransition.SlideOutHorizontallyExitBackward
                else -> ExitTransition.None
            }
        },
    ) {
        GroceryListsDashboardRoute(
            navigateToGroceryListScreen = navigateToGroceryListScreen,
            navigateToSettingsScreen = navigateToSettingsScreen,
        )
    }
}
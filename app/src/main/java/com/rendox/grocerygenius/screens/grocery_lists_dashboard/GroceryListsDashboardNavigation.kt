package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
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
) {
    composable(
        route = GROCERY_LISTS_DASHBOARD_ROUTE,
        enterTransition = {
            GroceryGeniusTransition.sharedZAxisEnterBackward
        },
    ) {
        GroceryListsDashboardRoute(
            navigateToGroceryListScreen = navigateToGroceryListScreen
        )
    }
}
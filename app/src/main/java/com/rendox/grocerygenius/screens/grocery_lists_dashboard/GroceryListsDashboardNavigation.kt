package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rendox.grocerygenius.ui.GroceryGeniusTransition

const val GroceryListsDashboardRoute = "grocery_lists_dashboard_route"

fun NavGraphBuilder.groceryListsDashboardScreen(
    navigateToGroceryListScreen: (String) -> Unit,
) {
    composable(
        route = GroceryListsDashboardRoute,
        enterTransition = {
            GroceryGeniusTransition.sharedZAxisEnterBackward
        },
    ) {
        GroceryListsDashboardRoute(
            navigateToGroceryListScreen = navigateToGroceryListScreen
        )
    }
}
package com.rendox.grocerygenius.feature.grocery_list.grocery_list_scren

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.rendox.grocerygenius.feature.grocery_list.GroceryListsSharedViewModel
import com.rendox.grocerygenius.feature.grocery_list.dashboard_screen.GROCERY_LISTS_DASHBOARD_ROUTE
import com.rendox.grocerygenius.ui.GroceryGeniusTransition

const val GROCERY_LIST_ROUTE = "grocery_list_route"

fun NavController.navigateToGroceryList(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    this.navigate(
        route = GROCERY_LIST_ROUTE,
        builder = navOptions,
    )
}

fun NavGraphBuilder.groceryListScreen(
    navigateBack: () -> Unit,
    navigateToCategoryScreen: (String, String) -> Unit,
    findViewModel: @Composable (NavBackStackEntry) -> GroceryListsSharedViewModel,
) {
    composable(
        route = GROCERY_LIST_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                GROCERY_LISTS_DASHBOARD_ROUTE -> GroceryGeniusTransition.SlideInHorizontallyEnterForward
                else -> GroceryGeniusTransition.SlideInHorizontallyEnterBackward
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                GROCERY_LISTS_DASHBOARD_ROUTE -> GroceryGeniusTransition.SlideOutHorizontallyExitForward
                else -> GroceryGeniusTransition.SlideOutHorizontallyExitBackward
            }
        },
    ) { backStackEntry ->
        val groceryListViewModel = findViewModel(backStackEntry)
        GroceryListRoute(
            groceryListViewModel = groceryListViewModel,
            navigateBack = navigateBack,
            navigateToCategoryScreen = navigateToCategoryScreen,
        )
    }
}

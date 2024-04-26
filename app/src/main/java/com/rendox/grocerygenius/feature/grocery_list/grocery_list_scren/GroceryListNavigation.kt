package com.rendox.grocerygenius.feature.grocery_list.grocery_list_scren

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.rendox.grocerygenius.feature.grocery_list.GroceryListsSharedViewModel
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
    findViewModel: @Composable (NavBackStackEntry) -> GroceryListsSharedViewModel,
) {
    composable(
        route = GROCERY_LIST_ROUTE,
        enterTransition = { GroceryGeniusTransition.SlideInHorizontallyEnterForward },
        exitTransition = { GroceryGeniusTransition.SlideOutHorizontallyExitForward },
    ) { backStackEntry ->
        val groceryListViewModel = findViewModel(backStackEntry)
        GroceryListRoute(
            groceryListViewModel = groceryListViewModel,
            navigateBack = navigateBack,
        )
    }
}

package com.rendox.grocerygenius.screens.grocery_list

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val GroceryListIdArg = "groceryListId"
const val GroceryListRoute = "grocery_list_route"

fun NavController.navigateToGroceryList(
    navOptions: NavOptionsBuilder.() -> Unit = {},
    listId: String,
) {
    this.navigate(
        route = "$GroceryListRoute/$listId",
        builder = navOptions,
    )
}

fun NavGraphBuilder.groceryListScreen() {
    composable(
        route = "$GroceryListRoute/{$GroceryListIdArg}",
        arguments = listOf(
            navArgument(GroceryListIdArg) { type = NavType.StringType },
        ),
    ) {
        GroceryListRoute()
    }
}

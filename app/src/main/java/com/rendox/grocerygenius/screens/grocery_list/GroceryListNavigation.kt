package com.rendox.grocerygenius.screens.grocery_list

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rendox.grocerygenius.ui.GroceryGeniusTransition

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

fun NavGraphBuilder.groceryListScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = "$GroceryListRoute/{$GroceryListIdArg}",
        arguments = listOf(
            navArgument(GroceryListIdArg) {
                type = NavType.StringType
                defaultValue = "sample-grocery-list-0"
            },
        ),
        enterTransition = {
            GroceryGeniusTransition.sharedZAxisEnterForward
        },
        exitTransition = {
            GroceryGeniusTransition.sharedZAxisExitBackward
        },
    ) {
        GroceryListRoute(navigateBack = navigateBack)
    }
}

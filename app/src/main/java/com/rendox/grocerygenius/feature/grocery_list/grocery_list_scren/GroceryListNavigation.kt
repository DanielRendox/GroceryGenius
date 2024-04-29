package com.rendox.grocerygenius.feature.grocery_list.grocery_list_scren

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rendox.grocerygenius.feature.grocery_list.dashboard_screen.GROCERY_LISTS_DASHBOARD_ROUTE
import com.rendox.grocerygenius.ui.GroceryGeniusTransition

const val GROCERY_LIST_ROUTE = "grocery_list_route"
const val GROCERY_LIST_SCREEN_LIST_ID_NAV_ARG = "grocery_list_screen_list_id_nav_arg"

fun NavController.navigateToGroceryList(
    groceryListId: String,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    this.navigate(
        route = "$GROCERY_LIST_ROUTE/$groceryListId",
        builder = navOptions,
    )
}

fun NavGraphBuilder.groceryListScreen(
    defaultGroceryListId: String?,
    navigateBack: () -> Unit,
    navigateToCategoryScreen: (String, String) -> Unit,
) {
    composable(
        route = "$GROCERY_LIST_ROUTE/{$GROCERY_LIST_SCREEN_LIST_ID_NAV_ARG}",
        arguments = listOf(
            navArgument(GROCERY_LIST_SCREEN_LIST_ID_NAV_ARG) {
                type = NavType.StringType
                defaultValue = defaultGroceryListId ?: ""
            }
        ),
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
    ) {
        GroceryListRoute(
            navigateBack = navigateBack,
            navigateToCategoryScreen = navigateToCategoryScreen,
        )
    }
}

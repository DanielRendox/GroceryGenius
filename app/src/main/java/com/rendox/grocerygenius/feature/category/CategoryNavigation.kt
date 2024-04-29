package com.rendox.grocerygenius.feature.category

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rendox.grocerygenius.ui.GroceryGeniusTransition

const val CATEGORY_ID_NAV_ARG = "category_id_nav_arg"
const val GROCERY_LIST_ID_NAV_ARG = "grocery_list_id_nav_arg"
const val CATEGORY_ROUTE = "category_route"

fun NavController.navigateToCategory(
    categoryId: String,
    groceryListId: String,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    this.navigate(
        route = "$CATEGORY_ROUTE/$categoryId/$groceryListId",
        builder = navOptions,
    )
}

fun NavGraphBuilder.categoryScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = "$CATEGORY_ROUTE/{$CATEGORY_ID_NAV_ARG}/{$GROCERY_LIST_ID_NAV_ARG}",
        arguments = listOf(
            navArgument(CATEGORY_ID_NAV_ARG) {
                type = NavType.StringType
            }
        ),
        enterTransition = {
            GroceryGeniusTransition.SlideInHorizontallyEnterForward
        },
        exitTransition = {
            GroceryGeniusTransition.SlideOutHorizontallyExitForward
        },
    ) {
        CategoryRoute(
            navigateBack = navigateBack,
        )
    }
}
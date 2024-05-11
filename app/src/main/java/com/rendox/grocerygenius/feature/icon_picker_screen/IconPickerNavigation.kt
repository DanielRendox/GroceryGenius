package com.rendox.grocerygenius.feature.icon_picker_screen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rendox.grocerygenius.ui.GroceryGeniusTransition

const val ICON_PICKER_ROUTE = "icon_picker_route"
const val PRODUCT_ID_ARG = "edit_grocery_id_arg"
const val ICON_PICKER_GROCERY_LIST_ID_ARG = "grocery_list_id_arg"
const val ICON_PICKER_ROUTE_WITH_ARGS = "$ICON_PICKER_ROUTE/{$PRODUCT_ID_ARG}/{$ICON_PICKER_GROCERY_LIST_ID_ARG}"

fun NavController.navigateToIconPicker(
    editProductId: String,
    groceryListId: String,
    navOptions: (NavOptionsBuilder.() -> Unit) = {},
) {
    this.navigate(
        route = "$ICON_PICKER_ROUTE/$editProductId/$groceryListId",
        builder = navOptions,
    )
}

fun NavGraphBuilder.iconPickerScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = ICON_PICKER_ROUTE_WITH_ARGS,
        enterTransition = { GroceryGeniusTransition.SlideInVertically },
        exitTransition = { GroceryGeniusTransition.SlideOutVertically },
        arguments = listOf(
            navArgument(PRODUCT_ID_ARG) {
                type = NavType.StringType
            },
            navArgument(ICON_PICKER_GROCERY_LIST_ID_ARG) {
                type = NavType.StringType
            },
        ),
    ) {
        IconPickerRoute(
            navigateBack = navigateBack,
        )
    }
}

package com.rendox.grocerygenius.feature.grocery_list

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.rendox.grocerygenius.feature.dashboard_screen.GROCERY_LISTS_DASHBOARD_ROUTE
import com.rendox.grocerygenius.ui.GroceryGeniusTransition

const val GROCERY_LIST_ROUTE = "grocery_list_route"
const val CATEGORY_ROUTE = "category_route"
const val GROCERY_LIST_CATEGORY_NESTED_NAV_ROUTE = "grocery_list_category_nested_nav_route"
const val GROCERY_LIST_ID_ARG = "grocery_list_id_arg"
const val GROCERY_LIST_CATEGORY_NESTED_NAV_ROUTE_WITH_ARGS =
    "$GROCERY_LIST_CATEGORY_NESTED_NAV_ROUTE/{$GROCERY_LIST_ID_ARG}"

fun NavController.navigateToGroceryList(
    groceryListId: String,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    this.navigate(
        route = "$GROCERY_LIST_CATEGORY_NESTED_NAV_ROUTE/$groceryListId",
        builder = navOptions,
    )
}

fun NavGraphBuilder.groceryListNestedNavigation(
    navController: NavController,
    defaultGroceryListId: String?,
    navigateBack: () -> Unit,
    navigateToIconPicker: (String, String) -> Unit,
) = navigation(
    startDestination = GROCERY_LIST_ROUTE,
    route = GROCERY_LIST_CATEGORY_NESTED_NAV_ROUTE_WITH_ARGS,
    arguments = listOf(
        navArgument(GROCERY_LIST_ID_ARG) {
            type = NavType.StringType
        }
    ),
) {
    composable(
        route = GROCERY_LIST_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                GROCERY_LISTS_DASHBOARD_ROUTE -> GroceryGeniusTransition.SharedZAxisEnterForward
                CATEGORY_ROUTE -> GroceryGeniusTransition.SlideInHorizontallyEnterBackward
                else -> EnterTransition.None
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                CATEGORY_ROUTE -> GroceryGeniusTransition.SlideOutHorizontallyExitBackward
                else -> ExitTransition.None
            }
        },
    ) { backStackEntry ->
        GroceryListRoute(
            navigateBack = navigateBack,
            groceryListViewModel = backStackEntry.groceryListCategorySharedViewModel(
                navController, defaultGroceryListId
            ),
            navigateToCategoryScreen = { navController.navigate(CATEGORY_ROUTE) },
            navigateToIconPicker = navigateToIconPicker,
        )
    }

    composable(
        route = CATEGORY_ROUTE,
        enterTransition = {
            GroceryGeniusTransition.SlideInHorizontallyEnterForward
        },
        exitTransition = {
            GroceryGeniusTransition.SlideOutHorizontallyExitForward
        },
    ) { backStackEntry ->
        CategoryRoute(
            navigateBack = { navController.popBackStack() },
            viewModel = backStackEntry.groceryListCategorySharedViewModel(
                navController, defaultGroceryListId
            ),
        )
    }
}

@Composable
fun NavBackStackEntry.groceryListCategorySharedViewModel(
    navController: NavController,
    defaultGroceryListId: String?,
): GroceryListViewModel {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    val listId = parentEntry.arguments?.getString(GROCERY_LIST_ID_ARG) ?: defaultGroceryListId ?: ""
    return hiltViewModel<GroceryListViewModel, GroceryListViewModel.Factory>(
        viewModelStoreOwner = parentEntry
    ) { factory ->
        factory.create(listId)
    }
}
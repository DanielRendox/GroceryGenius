package com.rendox.grocerygenius

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.rendox.grocerygenius.feature.category.categoryScreen
import com.rendox.grocerygenius.feature.category.navigateToCategory
import com.rendox.grocerygenius.feature.grocery_list.GroceryListsSharedViewModel
import com.rendox.grocerygenius.feature.grocery_list.dashboard_screen.GROCERY_LISTS_DASHBOARD_ROUTE
import com.rendox.grocerygenius.feature.grocery_list.dashboard_screen.groceryListsDashboardScreen
import com.rendox.grocerygenius.feature.grocery_list.dashboard_screen.navigateToGroceryListsDashboard
import com.rendox.grocerygenius.feature.grocery_list.grocery_list_scren.GROCERY_LIST_ROUTE
import com.rendox.grocerygenius.feature.grocery_list.grocery_list_scren.groceryListScreen
import com.rendox.grocerygenius.feature.grocery_list.grocery_list_scren.navigateToGroceryList
import com.rendox.grocerygenius.feature.settings.navigateToSettings
import com.rendox.grocerygenius.feature.settings.settingsScreen

const val GROCERY_LISTS_SHARED_NAVIGATION_ROUTE = "grocery_lists_shared_navigation_route"

@Composable
fun GroceryGeniusNavHost(startDestination: String) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = GROCERY_LISTS_SHARED_NAVIGATION_ROUTE,
        modifier = Modifier.fillMaxSize(),
    ) {
        navigation(
            startDestination = startDestination,
            route = GROCERY_LISTS_SHARED_NAVIGATION_ROUTE,
        ) {
            groceryListScreen(
                navigateBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        navController.navigateToGroceryListsDashboard {
                            popUpTo(route = GROCERY_LIST_ROUTE) {
                                inclusive = true
                            }
                        }
                    }
                },
                navigateToCategoryScreen = { categoryId, groceryListId ->
                    navController.navigateToCategory(
                        categoryId, groceryListId
                    )
                },
                findViewModel = { backStackEntry ->
                    backStackEntry.groceryListsSharedViewModel(navController)
                },
            )
            groceryListsDashboardScreen(
                navigateToGroceryListScreen = {
                    navController.navigateToGroceryList {
                        popUpTo(route = GROCERY_LISTS_DASHBOARD_ROUTE) {
                            saveState = true
                        }
                    }
                },
                navigateToSettingsScreen = {
                    navController.navigateToSettings()
                },
                findViewModel = { backStackEntry ->
                    backStackEntry.groceryListsSharedViewModel(navController)
                },
            )
        }
        settingsScreen(
            navigateBack = {
                navController.popBackStack()
            }
        )
        categoryScreen(
            navigateBack = {
                navController.popBackStack()
            }
        )
    }
}

@Composable
fun NavBackStackEntry.groceryListsSharedViewModel(
    navController: NavController,
): GroceryListsSharedViewModel {
    println("SharedViewModelDebug groceryListsSharedViewModel destination: ${destination.parent?.route}")
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(viewModelStoreOwner = parentEntry)
}
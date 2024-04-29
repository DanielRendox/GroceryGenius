package com.rendox.grocerygenius

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rendox.grocerygenius.feature.category.categoryScreen
import com.rendox.grocerygenius.feature.category.navigateToCategory
import com.rendox.grocerygenius.feature.grocery_list.dashboard_screen.GROCERY_LISTS_DASHBOARD_ROUTE
import com.rendox.grocerygenius.feature.grocery_list.dashboard_screen.groceryListsDashboardScreen
import com.rendox.grocerygenius.feature.grocery_list.dashboard_screen.navigateToGroceryListsDashboard
import com.rendox.grocerygenius.feature.grocery_list.grocery_list_scren.GROCERY_LIST_ROUTE
import com.rendox.grocerygenius.feature.grocery_list.grocery_list_scren.GROCERY_LIST_SCREEN_LIST_ID_NAV_ARG
import com.rendox.grocerygenius.feature.grocery_list.grocery_list_scren.groceryListScreen
import com.rendox.grocerygenius.feature.grocery_list.grocery_list_scren.navigateToGroceryList
import com.rendox.grocerygenius.feature.settings.navigateToSettings
import com.rendox.grocerygenius.feature.settings.settingsScreen

@Composable
fun GroceryGeniusNavHost(
    modifier: Modifier = Modifier,
    startDestination: String,
    defaultGroceryListId: String?,
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
    ) {
        groceryListScreen(
            defaultGroceryListId = defaultGroceryListId,
            navigateBack = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } else {
                    navController.navigateToGroceryListsDashboard {
                        popUpTo(route = "$GROCERY_LIST_ROUTE/{$GROCERY_LIST_SCREEN_LIST_ID_NAV_ARG}") {
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
        )
        groceryListsDashboardScreen(
            navigateToGroceryListScreen = { groceryListId ->
                navController.navigateToGroceryList(groceryListId) {
                    popUpTo(route = GROCERY_LISTS_DASHBOARD_ROUTE) {
                        saveState = true
                    }
                }
            },
            navigateToSettingsScreen = {
                navController.navigateToSettings()
            },
        )
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

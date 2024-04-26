package com.rendox.grocerygenius

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rendox.grocerygenius.screens.grocery_list.GROCERY_LIST_ID_ARG
import com.rendox.grocerygenius.screens.grocery_list.GROCERY_LIST_ROUTE
import com.rendox.grocerygenius.screens.grocery_list.groceryListScreen
import com.rendox.grocerygenius.screens.grocery_list.navigateToGroceryList
import com.rendox.grocerygenius.screens.grocery_lists_dashboard.GROCERY_LISTS_DASHBOARD_ROUTE
import com.rendox.grocerygenius.screens.grocery_lists_dashboard.groceryListsDashboardScreen
import com.rendox.grocerygenius.screens.grocery_lists_dashboard.navigateToGroceryListsDashboard
import com.rendox.grocerygenius.screens.settings.navigateToSettings
import com.rendox.grocerygenius.screens.settings.settingsScreen

@Composable
fun GroceryGeniusNavHost(
    startDestination: String,
    updateStartDestination: (String) -> Unit,
    defaultListId: String?,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize(),
    ) {
        groceryListsDashboardScreen(
            navigateToGroceryListScreen = {
                navController.navigateToGroceryList(listId = it) {
                    popUpTo(route = GROCERY_LISTS_DASHBOARD_ROUTE) {
                        saveState = true
                    }
                }
            },
            navigateToSettingsScreen = {
                navController.navigateToSettings()
            },
        )
        groceryListScreen(
            navigateBack = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } else {
                    updateStartDestination(GROCERY_LISTS_DASHBOARD_ROUTE)
                    navController.navigateToGroceryListsDashboard {
                        popUpTo(route = "$GROCERY_LIST_ROUTE/{$GROCERY_LIST_ID_ARG}") {
                            inclusive = true
                        }
                    }
                }
            },
            defaultListId = defaultListId,
        )
        settingsScreen(
            navigateBack = {
                navController.popBackStack()
            }
        )
    }
}
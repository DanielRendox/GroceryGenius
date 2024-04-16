package com.rendox.grocerygenius

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rendox.grocerygenius.screens.grocery_list.groceryListScreen
import com.rendox.grocerygenius.screens.grocery_list.navigateToGroceryList
import com.rendox.grocerygenius.screens.grocery_lists_dashboard.GROCERY_LISTS_DASHBOARD_ROUTE
import com.rendox.grocerygenius.screens.grocery_lists_dashboard.groceryListsDashboardScreen
import com.rendox.grocerygenius.screens.grocery_lists_dashboard.navigateToGroceryListsDashboard

@Composable
fun GroceryGeniusNavHost(
    startDestination: String = GROCERY_LISTS_DASHBOARD_ROUTE,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize(),
    ) {
        groceryListsDashboardScreen(
            navigateToGroceryListScreen = {
                navController.navigateToGroceryList(listId = it)
            }
        )
        groceryListScreen(
            navigateBack = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } else {
                    navController.navigateToGroceryListsDashboard()
                }
            }
        )
    }
}
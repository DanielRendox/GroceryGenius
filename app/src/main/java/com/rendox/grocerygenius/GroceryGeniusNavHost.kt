package com.rendox.grocerygenius

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rendox.grocerygenius.screens.grocery_list.groceryListScreen
import com.rendox.grocerygenius.screens.grocery_list.navigateToGroceryList
import com.rendox.grocerygenius.screens.grocery_lists_dashboard.GroceryListsDashboardRoute
import com.rendox.grocerygenius.screens.grocery_lists_dashboard.groceryListsDashboardScreen
import com.rendox.grocerygenius.screens.grocery_lists_dashboard.navigateToGroceryListsDashboard

@Composable
fun GroceryGeniusNavHost(
    modifier: Modifier = Modifier,
    startDestination: String = GroceryListsDashboardRoute,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
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
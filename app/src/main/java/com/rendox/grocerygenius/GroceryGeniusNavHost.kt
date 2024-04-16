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

@Composable
fun GroceryGeniusNavHost(
    defaultListId: String? = null,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (defaultListId != null) {
            "$GROCERY_LIST_ROUTE/{$GROCERY_LIST_ID_ARG}"
        } else {
            GROCERY_LISTS_DASHBOARD_ROUTE
        },
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
                    navController.navigateToGroceryListsDashboard {
                        popUpTo(route = "$GROCERY_LIST_ROUTE/{$GROCERY_LIST_ID_ARG}") {
                            inclusive = true
                        }
                    }
                }
            },
            defaultListId = defaultListId,
        )
    }
}
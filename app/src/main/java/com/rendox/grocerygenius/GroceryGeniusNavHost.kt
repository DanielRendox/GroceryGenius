package com.rendox.grocerygenius

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rendox.grocerygenius.feature.dashboard_screen.GROCERY_LISTS_DASHBOARD_ROUTE
import com.rendox.grocerygenius.feature.dashboard_screen.groceryListsDashboardScreen
import com.rendox.grocerygenius.feature.dashboard_screen.navigateToGroceryListsDashboard
import com.rendox.grocerygenius.feature.grocery_list.GROCERY_LIST_CATEGORY_NESTED_NAV_ROUTE_WITH_ARGS
import com.rendox.grocerygenius.feature.grocery_list.GROCERY_LIST_ROUTE
import com.rendox.grocerygenius.feature.grocery_list.groceryListNestedNavigation
import com.rendox.grocerygenius.feature.grocery_list.navigateToGroceryList
import com.rendox.grocerygenius.feature.icon_picker_screen.iconPickerScreen
import com.rendox.grocerygenius.feature.icon_picker_screen.navigateToIconPicker
import com.rendox.grocerygenius.feature.settings.navigateToSettings
import com.rendox.grocerygenius.feature.settings.settingsScreen

@Composable
fun GroceryGeniusNavHost(
    modifier: Modifier = Modifier,
    startDestination: String = GROCERY_LISTS_DASHBOARD_ROUTE,
    defaultGroceryListId: String? = null,
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
    ) {
        groceryListsDashboardScreen(
            navigateToGroceryListScreen = { groceryListId ->
                if (navController.currentDestination?.route == GROCERY_LISTS_DASHBOARD_ROUTE) {
                    navController.navigateToGroceryList(groceryListId)
                }
            },
            navigateToSettingsScreen = {
                if (navController.currentDestination?.route == GROCERY_LISTS_DASHBOARD_ROUTE) {
                    navController.navigateToSettings()
                }
            },
        )
        groceryListNestedNavigation(
            navController = navController,
            defaultGroceryListId = defaultGroceryListId,
            navigateBack = {
                if (navController.currentDestination?.route == GROCERY_LIST_ROUTE) {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        navController.navigateToGroceryListsDashboard {
                            popUpTo(route = GROCERY_LIST_CATEGORY_NESTED_NAV_ROUTE_WITH_ARGS) {
                                inclusive = true
                            }
                        }
                    }
                }
            },
            navigateToIconPicker = { editProductId, groceryListId ->
                navController.navigateToIconPicker(
                    editProductId = editProductId,
                    groceryListId = groceryListId,
                )
            },
        )
        settingsScreen(
            navigateBack = { navController.popBackStack() },
        )
        iconPickerScreen(
            navigateBack = { navController.popBackStack() },
        )
    }
}

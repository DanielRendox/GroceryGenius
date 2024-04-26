package com.rendox.grocerygenius

import com.rendox.grocerygenius.feature.grocery_list.dashboard_screen.GROCERY_LISTS_DASHBOARD_ROUTE
import com.rendox.grocerygenius.model.DEFAULT_USER_PREFERENCES
import com.rendox.grocerygenius.model.DarkThemeConfig
import com.rendox.grocerygenius.model.GroceryGeniusColorScheme

data class MainActivityUiState(
    val defaultListId: String? = null,
    val startDestinationRoute: String = GROCERY_LISTS_DASHBOARD_ROUTE,
    val darkThemeConfig: DarkThemeConfig = DEFAULT_USER_PREFERENCES.darkThemeConfig,
    val useSystemAccentColor: Boolean = DEFAULT_USER_PREFERENCES.useSystemAccentColor,
    val selectedTheme: GroceryGeniusColorScheme = DEFAULT_USER_PREFERENCES.selectedTheme,
)

package com.rendox.grocerygenius

import com.rendox.grocerygenius.model.DarkThemeConfig
import com.rendox.grocerygenius.model.GroceryGeniusColorScheme

data class MainActivityUiState(
    val defaultListId: String?,
    val startDestinationRoute: String,
    val darkThemeConfig: DarkThemeConfig,
    val useSystemAccentColor: Boolean,
    val selectedTheme: GroceryGeniusColorScheme,
)

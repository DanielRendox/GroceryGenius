package com.rendox.grocerygenius.feature.settings

import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.DEFAULT_USER_PREFERENCES
import com.rendox.grocerygenius.model.GroceryList
import com.rendox.grocerygenius.model.UserPreferences

data class SettingsScreenState(
    val userPreferences: UserPreferences = DEFAULT_USER_PREFERENCES,
    val groceryLists: List<GroceryList> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
)

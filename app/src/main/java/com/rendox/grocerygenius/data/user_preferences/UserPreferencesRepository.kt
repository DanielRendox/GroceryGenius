package com.rendox.grocerygenius.data.user_preferences

import com.rendox.grocerygenius.model.DarkThemeConfig
import com.rendox.grocerygenius.model.GroceryGeniusColorScheme
import com.rendox.grocerygenius.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun updateDefaultListId(listId: String?)
    suspend fun updateLastOpenedListId(listId: String)
    suspend fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig)
    suspend fun updateUseSystemAccentColor(useSystemAccentColor: Boolean)
    suspend fun updateOpenLastViewedList(openLastViewedList: Boolean)
    suspend fun updateSelectedTheme(selectedTheme: GroceryGeniusColorScheme)
}
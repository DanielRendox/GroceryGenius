package com.rendox.grocerygenius.screens.settings

import com.rendox.grocerygenius.model.DarkThemeConfig
import com.rendox.grocerygenius.model.GroceryGeniusColorScheme

sealed interface SettingsScreenIntent {
    data class ChangeDarkThemeConfig(val config: DarkThemeConfig) : SettingsScreenIntent
    data class ChangeUseSystemAccentColor(val use: Boolean) : SettingsScreenIntent
    data class OnChangeDefaultList(val listId: String?) : SettingsScreenIntent
    data class ChangeOpenLastViewedListConfig(val openLastViewedList: Boolean) : SettingsScreenIntent
    data class ChangeColorScheme(val scheme: GroceryGeniusColorScheme) : SettingsScreenIntent
}
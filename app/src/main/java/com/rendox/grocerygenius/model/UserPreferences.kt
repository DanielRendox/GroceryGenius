package com.rendox.grocerygenius.model

data class UserPreferences(
    val defaultListId: String? = DEFAULT_USER_PREFERENCES.defaultListId,
    val lastOpenedListId: String? = DEFAULT_USER_PREFERENCES.lastOpenedListId,
    val darkThemeConfig: DarkThemeConfig = DEFAULT_USER_PREFERENCES.darkThemeConfig,
    val useSystemAccentColor: Boolean = DEFAULT_USER_PREFERENCES.useSystemAccentColor,
    val openLastViewedList: Boolean = DEFAULT_USER_PREFERENCES.openLastViewedList,
    val selectedTheme: GroceryGeniusColorScheme = DEFAULT_USER_PREFERENCES.selectedTheme,
)

val DEFAULT_USER_PREFERENCES = UserPreferences(
    defaultListId = null,
    lastOpenedListId = null,
    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    useSystemAccentColor = true,
    openLastViewedList = true,
    selectedTheme = GroceryGeniusColorScheme.PurpleColorScheme,
)

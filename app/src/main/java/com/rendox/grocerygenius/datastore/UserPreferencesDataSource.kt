package com.rendox.grocerygenius.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rendox.grocerygenius.model.DarkThemeConfig
import com.rendox.grocerygenius.model.DEFAULT_USER_PREFERENCES
import com.rendox.grocerygenius.model.GroceryGeniusColorScheme
import com.rendox.grocerygenius.model.UserPreferences
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    /**
     * The id of the list that is opened on app startup.
     * When the user navigates to a different list, this property gets updated.
     * Alternatively, the user can explicitly set the list id in the settings.
     */
    val userPreferencesFlow = dataStore.data.map { preferences ->
        // unspecified list id is saved as an empty string since preferences data store
        // doesn't allow nullable values
        val defaultListId = preferences[DEFAULT_LIST_ID_KEY] ?: ""
        UserPreferences(
            defaultListId = defaultListId.ifEmpty { null },
            lastOpenedListId = preferences[LAST_OPENED_LIST_ID_KEY],
            darkThemeConfig = preferences[DARK_THEME_CONFIG_KEY]?.let {
                DarkThemeConfig.entries[it]
            } ?: DEFAULT_USER_PREFERENCES.darkThemeConfig,
            useSystemAccentColor = preferences[USE_SYSTEM_ACCENT_COLOR_KEY]
                ?: DEFAULT_USER_PREFERENCES.useSystemAccentColor,
            openLastViewedList = preferences[OPEN_LAST_VIEWED_LIST_KEY]
                ?: DEFAULT_USER_PREFERENCES.openLastViewedList,
            selectedTheme = preferences[SELECTED_THEME_KEY]?.let {
                GroceryGeniusColorScheme.entries[it]
            } ?: DEFAULT_USER_PREFERENCES.selectedTheme
        )
    }

    suspend fun updateDefaultListId(listId: String?) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_LIST_ID_KEY] = listId ?: ""
        }
    }

    suspend fun updateLastOpenedListId(listId: String) {
        dataStore.edit { preferences ->
            preferences[LAST_OPENED_LIST_ID_KEY] = listId
        }
    }

    suspend fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_CONFIG_KEY] = darkThemeConfig.ordinal
        }
    }

    suspend fun updateUseSystemAccentColor(useSystemAccentColor: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_SYSTEM_ACCENT_COLOR_KEY] = useSystemAccentColor
        }
    }

    suspend fun updateOpenLastViewedList(openLastViewedList: Boolean) {
        dataStore.edit { preferences ->
            preferences[OPEN_LAST_VIEWED_LIST_KEY] = openLastViewedList
        }
    }

    suspend fun updateSelectedTheme(selectedTheme: GroceryGeniusColorScheme) {
        dataStore.edit { preferences ->
            preferences[SELECTED_THEME_KEY] = selectedTheme.ordinal
        }
    }

    companion object {
        val DEFAULT_LIST_ID_KEY = stringPreferencesKey("default_list_id")
        val DARK_THEME_CONFIG_KEY = intPreferencesKey("dark_theme_config")
        val USE_SYSTEM_ACCENT_COLOR_KEY = booleanPreferencesKey("use_system_accent_color")
        val OPEN_LAST_VIEWED_LIST_KEY = booleanPreferencesKey("open_last_viewed_list")
        val SELECTED_THEME_KEY = intPreferencesKey("selected_theme")
        val LAST_OPENED_LIST_ID_KEY = stringPreferencesKey("last_opened_list_id")
    }
}
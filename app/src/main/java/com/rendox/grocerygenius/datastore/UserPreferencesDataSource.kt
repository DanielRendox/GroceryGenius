package com.rendox.grocerygenius.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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
    val defaultListId = dataStore.data.map { preferences ->
        preferences[DEFAULT_LIST_ID_KEY]
    }

    suspend fun updateDefaultListId(listId: String) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_LIST_ID_KEY] = listId
        }
    }

    companion object {
        val DEFAULT_LIST_ID_KEY = stringPreferencesKey("default_list_id")
    }
}
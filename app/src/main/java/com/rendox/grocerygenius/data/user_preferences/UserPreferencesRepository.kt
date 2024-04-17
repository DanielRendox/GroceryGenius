package com.rendox.grocerygenius.data.user_preferences

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    val defaultListId: Flow<String?>
    suspend fun updateDefaultListId(listId: String)
}
package com.rendox.grocerygenius.data.user_preferences

import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import com.rendox.grocerygenius.datastore.UserPreferencesDataSource
import com.rendox.grocerygenius.model.DarkThemeConfig
import com.rendox.grocerygenius.model.GroceryGeniusColorScheme
import com.rendox.grocerygenius.model.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val groceryListRepository: GroceryListRepository,
) : UserPreferencesRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val userPreferencesFlow: Flow<UserPreferences>
        get() = userPreferencesDataSource.userPreferencesFlow
            .flatMapLatest { userPreferences ->
                if (userPreferences.defaultListId != null) {
                    groceryListRepository
                        .getGroceryListById(userPreferences.defaultListId)
                        .map { groceryList ->
                            userPreferences.copy(defaultListId = groceryList?.id)
                        }
                } else flowOf(userPreferences)
            }

    override suspend fun updateDefaultListId(listId: String?) {
        userPreferencesDataSource.updateDefaultListId(listId)
    }

    override suspend fun updateLastOpenedListId(listId: String) {
        userPreferencesDataSource.updateLastOpenedListId(listId)
    }

    override suspend fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferencesDataSource.updateDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun updateUseSystemAccentColor(useSystemAccentColor: Boolean) {
        userPreferencesDataSource.updateUseSystemAccentColor(useSystemAccentColor)
    }

    override suspend fun updateOpenLastViewedList(openLastViewedList: Boolean) {
        userPreferencesDataSource.updateOpenLastViewedList(openLastViewedList)
    }

    override suspend fun updateSelectedTheme(selectedTheme: GroceryGeniusColorScheme) {
        userPreferencesDataSource.updateSelectedTheme(selectedTheme)
    }
}
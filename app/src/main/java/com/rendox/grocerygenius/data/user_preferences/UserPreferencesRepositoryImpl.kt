package com.rendox.grocerygenius.data.user_preferences

import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import com.rendox.grocerygenius.datastore.UserPreferencesDataSource
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
    override val defaultListId: Flow<String?>
        get() = userPreferencesDataSource.defaultListId
            .flatMapLatest {
                it?.let { listId ->
                    groceryListRepository.getGroceryListById(listId)
                } ?: flowOf(null)
            }
            .map { it?.id }

    override suspend fun updateDefaultListId(listId: String) {
        userPreferencesDataSource.updateDefaultListId(listId)
    }
}
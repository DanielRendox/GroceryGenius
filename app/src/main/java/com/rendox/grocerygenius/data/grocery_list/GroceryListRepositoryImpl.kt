package com.rendox.grocerygenius.data.grocery_list

import com.rendox.grocerygenius.data.model.asEntity
import com.rendox.grocerygenius.database.grocery_list.GroceryListDao
import com.rendox.grocerygenius.model.GroceryList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroceryListRepositoryImpl @Inject constructor(
    private val groceryListDao: GroceryListDao,
) : GroceryListRepository {
    override suspend fun insertGroceryList(groceryList: GroceryList) {
        groceryListDao.insertGroceryList(groceryList.asEntity())
    }

    override fun getGroceryListById(id: String): Flow<GroceryList?> {
        return groceryListDao.getGroceryListById(id)
    }

    override fun getAllGroceryLists(): Flow<List<GroceryList>> {
        return groceryListDao.getAllGroceryLists()
    }

    override suspend fun updateGroceryListName(listId: String, name: String) {
        groceryListDao.updateGroceryListName(listId, name)
    }

    override suspend fun deleteGroceryListById(groceryListId: String) {
        groceryListDao.deleteGroceryListById(groceryListId)
    }

    override suspend fun upsertGroceryLists(groceryLists: List<GroceryList>) {
        groceryListDao.upsertGroceryLists(groceryLists.map { it.asEntity() })
    }

    override suspend fun updateGroceryLists(groceryLists: List<GroceryList>) {
        groceryListDao.updateGroceryLists(groceryLists.map { it.asEntity() })
    }
}
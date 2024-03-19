package com.rendox.grocerygenius.data.grocery_list

import com.rendox.grocerygenius.data.model.asEntity
import com.rendox.grocerygenius.data.model.asExternalModel
import com.rendox.grocerygenius.database.grocery_list.GroceryListDao
import com.rendox.grocerygenius.model.GroceryList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroceryListRepositoryImpl @Inject constructor(
    private val groceryListDao: GroceryListDao,
) : GroceryListRepository {
    override suspend fun insertGroceryList(groceryList: GroceryList) {
        groceryListDao.insertGroceryList(groceryList.asEntity())
    }

    override fun getGroceryListById(id: Int): Flow<GroceryList> {
        return groceryListDao.getGroceryListById(id).map { it.asExternalModel() }
    }

    override fun getAllGroceryLists(): Flow<List<GroceryList>> {
        return groceryListDao.getAllGroceryLists().map {  groceryList ->
            groceryList.map { it.asExternalModel() }
        }
    }

    override suspend fun updateGroceryList(groceryList: GroceryList) {
        groceryListDao.updateGroceryList(groceryList.asEntity())
    }

    override suspend fun deleteGroceryList(groceryList: GroceryList) {
        groceryListDao.deleteGroceryList(groceryList.asEntity())
    }
}
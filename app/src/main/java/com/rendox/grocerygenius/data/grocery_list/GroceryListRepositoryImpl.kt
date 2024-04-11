package com.rendox.grocerygenius.data.grocery_list

import com.rendox.grocerygenius.data.Synchronizer
import com.rendox.grocerygenius.data.model.asEntity
import com.rendox.grocerygenius.data.model.asExternalModel
import com.rendox.grocerygenius.data.suspendRunCatching
import com.rendox.grocerygenius.database.grocery_list.GroceryListDao
import com.rendox.grocerygenius.model.GroceryList
import com.rendox.grocerygenius.network.grocery_list.GroceryListNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroceryListRepositoryImpl @Inject constructor(
    private val groceryListDao: GroceryListDao,
    private val groceryListNetworkDataSource: GroceryListNetworkDataSource,
) : GroceryListRepository {
    override suspend fun insertGroceryList(groceryList: GroceryList) {
        groceryListDao.insertGroceryList(groceryList.asEntity())
    }

    override fun getGroceryListById(id: String): Flow<GroceryList?> {
        return groceryListDao.getGroceryListById(id).map { it?.asExternalModel() }
    }

    override fun getAllGroceryLists(): Flow<List<GroceryList>> {
        return groceryListDao.getAllGroceryLists().map {  groceryList ->
            groceryList.map { it.asExternalModel() }
        }
    }

    override suspend fun updateGroceryList(groceryList: GroceryList) {
        groceryListDao.updateGroceryList(groceryList.asEntity())
    }

    override suspend fun deleteGroceryListById(groceryListId: String) {
        groceryListDao.deleteGroceryListById(groceryListId)
    }

    override suspend fun syncWith(synchronizer: Synchronizer) = suspendRunCatching {
        val existingGroceryLists = groceryListDao.getAllGroceryLists().first()
        if (existingGroceryLists.isEmpty()) {
            val groceryLists = groceryListNetworkDataSource.getAllGroceryLists()
            groceryListDao.upsertGroceryLists(groceryLists.map { it.asEntity() })
        }
    }.isSuccess
}
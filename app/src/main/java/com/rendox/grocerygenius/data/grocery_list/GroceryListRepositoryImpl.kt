package com.rendox.grocerygenius.data.grocery_list

import com.rendox.grocerygenius.data.Synchronizer
import com.rendox.grocerygenius.data.model.asEntity
import com.rendox.grocerygenius.data.suspendRunCatching
import com.rendox.grocerygenius.database.grocery.GroceryDao
import com.rendox.grocerygenius.database.grocery.GroceryEntity
import com.rendox.grocerygenius.database.grocery_list.GroceryListDao
import com.rendox.grocerygenius.database.product.ProductDao
import com.rendox.grocerygenius.model.GroceryList
import com.rendox.grocerygenius.network.grocery_list.GroceryListNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GroceryListRepositoryImpl @Inject constructor(
    private val groceryListDao: GroceryListDao,
    private val groceryListNetworkDataSource: GroceryListNetworkDataSource,
    private val groceryDao: GroceryDao,
    private val productDao: ProductDao,
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

    override suspend fun syncWith(synchronizer: Synchronizer) = suspendRunCatching {
        val existingGroceryLists = groceryListDao.getAllGroceryLists().first()
        if (existingGroceryLists.isEmpty()) {
            val groceryLists = groceryListNetworkDataSource.getAllGroceryLists()
            groceryListDao.upsertGroceryLists(groceryLists.map { it.asEntity() })
            val groceries = productDao.getAllProducts().map {
                GroceryEntity(
                    productId = it.id,
                    groceryListId = groceryLists.first().id,
                    purchased = false,
                    description = null,
                    purchasedLastModified = System.currentTimeMillis(),
                )
            }
            println("inserting sample groceries: $groceries")
            groceryDao.insertGroceries(groceries)
        }
    }.isSuccess
}
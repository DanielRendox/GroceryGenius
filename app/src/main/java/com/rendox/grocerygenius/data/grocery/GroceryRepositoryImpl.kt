package com.rendox.grocerygenius.data.grocery

import com.rendox.grocerygenius.database.grocery.GroceryDao
import com.rendox.grocerygenius.database.grocery.GroceryEntity
import com.rendox.grocerygenius.model.Grocery
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroceryRepositoryImpl @Inject constructor(
    private val groceryDao: GroceryDao
) : GroceryRepository {
    override suspend fun addGroceryToList(
        productId: Int,
        listId: Int,
        description: String?,
        purchased: Boolean,
        purchasedLastModified: Long,
    ) {
        groceryDao.insertGrocery(
            GroceryEntity(
                productId = productId,
                groceryListId = listId,
                description = description,
                purchased = purchased,
                purchasedLastModified = purchasedLastModified,
            )
        )
    }

    override suspend fun insertProductAndGrocery(
        name: String,
        iconUri: String?,
        categoryId: Int,
        groceryListId: Int,
        description: String?,
        purchased: Boolean,
        purchasedLastModified: Long,
    ) {
        groceryDao.insertProductAndGrocery(
            name = name,
            iconUri = iconUri,
            categoryId = categoryId,
            groceryListId = groceryListId,
            description = description,
            purchased = purchased,
            purchasedLastModified = purchasedLastModified,
        )
    }

    override fun getGroceriesFromList(listId: Int): Flow<List<Grocery>> {
        return groceryDao.getGroceriesFromList(listId)
    }

    override suspend fun getGroceryDescriptions(productId: Int): List<String> {
        return groceryDao.getGroceryDescriptions(productId)
    }

    override suspend fun updatePurchased(
        productId: Int,
        listId: Int,
        purchased: Boolean,
        purchasedLastModified: Long,
    ) {
        groceryDao.updatePurchased(
            productId,
            listId,
            purchased,
            purchasedLastModified,
        )
    }

    override suspend fun updateDescription(productId: Int, listId: Int, description: String) {
        groceryDao.updateDescription(productId, listId, description)
    }

    override suspend fun removeGroceryFromList(productId: Int, listId: Int) {
        groceryDao.deleteGrocery(productId, listId)
    }
}
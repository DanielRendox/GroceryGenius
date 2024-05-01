package com.rendox.grocerygenius.data.grocery

import com.rendox.grocerygenius.data.model.asExternalModel
import com.rendox.grocerygenius.database.grocery.GroceryDao
import com.rendox.grocerygenius.database.grocery.GroceryEntity
import com.rendox.grocerygenius.database.product.ProductDao
import com.rendox.grocerygenius.database.product.ProductEntity
import com.rendox.grocerygenius.model.Grocery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroceryRepositoryImpl @Inject constructor(
    private val groceryDao: GroceryDao,
    private val productDao: ProductDao,
) : GroceryRepository {
    override suspend fun addGroceryToList(
        productId: String,
        listId: String,
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
        productId: String,
        iconId: String?,
        categoryId: String?,
        groceryListId: String,
        description: String?,
        purchased: Boolean,
        purchasedLastModified: Long,
        isDefault: Boolean,
    ) {
        val product = ProductEntity(
            id = productId,
            name = name,
            categoryId = categoryId,
            iconFileName = iconId,
            isDefault = isDefault,
        )
        val grocery = GroceryEntity(
            productId = productId,
            groceryListId = groceryListId,
            description = description,
            purchased = purchased,
            purchasedLastModified = purchasedLastModified,
        )
        productDao.insertProduct(product)
        groceryDao.insertGrocery(grocery)
    }

    override fun getGroceriesFromList(listId: String): Flow<List<Grocery>> {
        return groceryDao.getGroceriesFromList(listId).map { combinedGroceries ->
            combinedGroceries.map { combinedGrocery ->
                combinedGrocery.asExternalModel()
            }
        }
    }

    override fun getGroceryById(productId: String, listId: String): Flow<Grocery?> {
        return groceryDao.getGrocery(productId, listId).map { it?.asExternalModel() }
    }

    override suspend fun updatePurchased(
        productId: String,
        listId: String,
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

    override suspend fun updateDescription(productId: String, listId: String, description: String?) {
        groceryDao.updateDescription(productId, listId, description)
    }

    override suspend fun removeGroceryFromList(productId: String, listId: String) {
        groceryDao.deleteGrocery(productId, listId)
    }
}
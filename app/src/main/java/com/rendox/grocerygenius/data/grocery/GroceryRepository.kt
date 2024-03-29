package com.rendox.grocerygenius.data.grocery

import com.rendox.grocerygenius.model.Grocery
import kotlinx.coroutines.flow.Flow

interface GroceryRepository {
    suspend fun addGroceryToList(
        productId: Int = 0,
        listId: Int,
        description: String? = null,
        purchased: Boolean,
        purchasedLastModified: Long = System.currentTimeMillis(),
    )

    suspend fun insertProductAndGrocery(
        name: String,
        iconUri: String?,
        categoryId: Int,
        groceryListId: Int,
        description: String?,
        purchased: Boolean,
        purchasedLastModified: Long = System.currentTimeMillis(),
    )

    fun getGroceriesFromList(listId: Int): Flow<List<Grocery>>
    suspend fun getGroceryDescriptions(productId: Int): List<String>

    suspend fun updatePurchased(
        productId: Int,
        listId: Int,
        purchased: Boolean,
        purchasedLastModified: Long = System.currentTimeMillis(),
    )

    suspend fun updateDescription(productId: Int, listId: Int, description: String)
    suspend fun removeGroceryFromList(productId: Int, listId: Int)
}
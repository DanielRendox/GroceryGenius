package com.rendox.grocerygenius.database.grocery

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.rendox.grocerygenius.database.product.ProductEntity
import com.rendox.grocerygenius.model.Grocery
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GroceryDao {
    @Insert
    abstract suspend fun insertGrocery(grocery: GroceryEntity)

    @Insert
    protected abstract suspend fun insertProduct(product: ProductEntity): Long

    @Transaction
    open suspend fun insertProductAndGrocery(
        name: String,
        iconUri: String?,
        categoryId: Int,
        groceryListId: Int,
        description: String?,
        purchased: Boolean,
        purchasedLastModified: Long = System.currentTimeMillis(),
    ) {
        val product = ProductEntity(
            name = name,
            iconUri = iconUri,
            categoryId = categoryId,
        )
        val productId = insertProduct(product)
        val grocery = GroceryEntity(
            productId = productId.toInt(),
            groceryListId = groceryListId,
            description = description,
            purchased = purchased,
            purchasedLastModified = purchasedLastModified,
        )
        insertGrocery(grocery)
    }

    @Query(
        """
        SELECT
            grocery.productId,
            product.name,
            grocery.purchased,
            grocery.description,
            product.iconUri,
            product.categoryId,
            grocery.purchasedLastModified
        FROM GroceryEntity grocery
        INNER JOIN ProductEntity product ON grocery.productId = product.id
        WHERE grocery.groceryListId = :listId
    """
    )
    abstract fun getGroceriesFromList(listId: Int): Flow<List<Grocery>>

    @Query(
        "SELECT DISTINCT description FROM GroceryEntity WHERE productId = :productId"
    )
    abstract suspend fun getGroceryDescriptions(productId: Int): List<String>

    @Query("""
        UPDATE GroceryEntity
        SET purchased = :purchased, purchasedLastModified = :purchasedLastModified
        WHERE productId = :productId AND groceryListId = :listId
    """)
    abstract suspend fun updatePurchased(
        productId: Int,
        listId: Int,
        purchased: Boolean,
        purchasedLastModified: Long,
    )

    @Query("""
        UPDATE GroceryEntity
        SET description = :description
        WHERE productId = :productId AND groceryListId = :listId
    """)
    abstract suspend fun updateDescription(productId: Int, listId: Int, description: String)

    @Query("DELETE FROM GroceryEntity WHERE productId = :productId AND groceryListId = :listId")
    abstract suspend fun deleteGrocery(productId: Int, listId: Int)
}
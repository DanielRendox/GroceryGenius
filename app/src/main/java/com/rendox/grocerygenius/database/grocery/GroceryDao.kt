package com.rendox.grocerygenius.database.grocery

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.rendox.grocerygenius.database.product.ProductEntity
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
        iconId: Int?,
        categoryId: Int?,
        groceryListId: Int,
        description: String?,
        purchased: Boolean,
        purchasedLastModified: Long = System.currentTimeMillis(),
        productIsDeletable: Boolean,
    ) {
        val product = ProductEntity(
            name = name,
            categoryId = categoryId,
            iconId = iconId,
            deletable = productIsDeletable,
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
            icon.id as iconId,
            icon.filePath as iconFilePath,
            category.id as categoryId,
            category.name as categoryName,
            category.sortingPriority as categorySortingPriority,
            grocery.purchasedLastModified
        FROM GroceryEntity grocery
        INNER JOIN ProductEntity product ON grocery.productId = product.id
        LEFT JOIN CategoryEntity category ON product.categoryId = category.id
        LEFT JOIN IconEntity icon ON product.iconId = icon.id
        WHERE grocery.groceryListId = :listId
    """
    )
    abstract fun getGroceriesFromList(listId: Int): Flow<List<CombinedGrocery>>

    @Query(
        """
        SELECT
            grocery.productId,
            product.name,
            grocery.purchased,
            grocery.description,
            icon.id as iconId,
            icon.filePath as iconFilePath,
            category.id as categoryId,
            category.name as categoryName,
            category.sortingPriority as categorySortingPriority,
            grocery.purchasedLastModified
        FROM GroceryEntity grocery
        INNER JOIN ProductEntity product ON grocery.productId = product.id
        LEFT JOIN CategoryEntity category ON product.categoryId = category.id
        LEFT JOIN IconEntity icon ON product.iconId = icon.id
        WHERE grocery.productId = :productId AND grocery.groceryListId = :listId
    """
    )
    abstract suspend fun getGrocery(productId: Int, listId: Int): CombinedGrocery?

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
    abstract suspend fun updateDescription(productId: Int, listId: Int, description: String?)

    @Query("DELETE FROM GroceryEntity WHERE productId = :productId AND groceryListId = :listId")
    abstract suspend fun deleteGrocery(productId: Int, listId: Int)
}
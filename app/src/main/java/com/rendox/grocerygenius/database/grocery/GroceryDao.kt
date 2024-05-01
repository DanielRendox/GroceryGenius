package com.rendox.grocerygenius.database.grocery

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {
    @Insert
    suspend fun insertGroceries(groceries: List<GroceryEntity>)

    @Insert
    suspend fun insertGrocery(grocery: GroceryEntity)

    @Query(
        """
        SELECT
            grocery.productId,
            product.name,
            grocery.purchased,
            grocery.description,
            icon.uniqueFileName as iconId,
            icon.filePath as iconFilePath,
            category.id as categoryId,
            category.name as categoryName,
            category.sortingPriority as categorySortingPriority,
            grocery.purchasedLastModified,
            product.isDefault as productIsDefault
        FROM GroceryEntity grocery
        INNER JOIN ProductEntity product ON grocery.productId = product.id
        LEFT JOIN CategoryEntity category ON product.categoryId = category.id
        LEFT JOIN IconEntity icon ON product.iconFileName = icon.uniqueFileName
        WHERE grocery.groceryListId = :listId
    """
    )
    fun getGroceriesFromList(listId: String): Flow<List<CombinedGrocery>>

    @Query(
        """
        SELECT
            grocery.productId,
            product.name,
            grocery.purchased,
            grocery.description,
            icon.uniqueFileName as iconId,
            icon.filePath as iconFilePath,
            category.id as categoryId,
            category.name as categoryName,
            category.sortingPriority as categorySortingPriority,
            grocery.purchasedLastModified,
            product.isDefault as productIsDefault
        FROM GroceryEntity grocery
        INNER JOIN ProductEntity product ON grocery.productId = product.id
        LEFT JOIN CategoryEntity category ON product.categoryId = category.id
        LEFT JOIN IconEntity icon ON product.iconFileName = icon.uniqueFileName
        WHERE grocery.productId = :productId AND grocery.groceryListId = :listId
    """
    )
    fun getGrocery(productId: String, listId: String): Flow<CombinedGrocery?>

    @Query("""
        UPDATE GroceryEntity
        SET purchased = :purchased, purchasedLastModified = :purchasedLastModified
        WHERE productId = :productId AND groceryListId = :listId
    """)
    suspend fun updatePurchased(
        productId: String,
        listId: String,
        purchased: Boolean,
        purchasedLastModified: Long,
    )

    @Query("""
        UPDATE GroceryEntity
        SET description = :description
        WHERE productId = :productId AND groceryListId = :listId
    """)
    suspend fun updateDescription(productId: String, listId: String, description: String?)

    @Query("DELETE FROM GroceryEntity WHERE productId = :productId AND groceryListId = :listId")
    suspend fun deleteGrocery(productId: String, listId: String)

    @Query("SELECT COUNT(productId) FROM GroceryEntity WHERE groceryListId = :listId")
    fun getNumOfGroceriesInList(listId: String): Flow<Int>
}
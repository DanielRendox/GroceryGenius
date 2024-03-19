package com.rendox.grocerygenius.database.grocery

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rendox.grocerygenius.model.Grocery
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {
    @Insert
    suspend fun insertGrocery(grocery: GroceryEntity)

    @Query(
        """
        SELECT
            grocery.productId,
            product.name,
            grocery.purchased,
            grocery.description,
            product.iconUri,
            category.id as chosenCategoryId
        FROM GroceryEntity grocery
        INNER JOIN ProductEntity product ON grocery.productId = product.id
        INNER JOIN CategoryEntity category ON product.categoryId = category.id
        WHERE grocery.groceryListId = :listId
    """
    )
    fun getGroceriesFromList(listId: Int): Flow<List<Grocery>>

    @Query(
        "SELECT DISTINCT description FROM GroceryEntity WHERE productId = :productId"
    )
    suspend fun getGroceryDescriptions(productId: Int): List<String>

    @Query("""
        UPDATE GroceryEntity
        SET purchased = :purchased
        WHERE productId = :productId AND groceryListId = :listId
    """)
    suspend fun updatePurchased(productId: Int, listId: Int, purchased: Boolean)

    @Query("""
        UPDATE GroceryEntity
        SET description = :description
        WHERE productId = :productId AND groceryListId = :listId
    """)
    suspend fun updateDescription(productId: Int, listId: Int, description: String)

    @Query("DELETE FROM GroceryEntity WHERE productId = :productId AND groceryListId = :listId")
    suspend fun deleteGrocery(productId: Int, listId: Int)
}
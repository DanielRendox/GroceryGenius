package com.rendox.grocerygenius.database.grocery_list

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.rendox.grocerygenius.model.GroceryList
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryListDao {
    @Upsert
    suspend fun upsertGroceryLists(groceryLists: List<GroceryListEntity>)

    @Insert
    suspend fun insertGroceryList(groceryList: GroceryListEntity)

    @Query(
        """
         SELECT 
             groceryList.id, 
             groceryList.name, 
             groceryList.sortingPriority, 
             COUNT(grocery.productId) as numOfGroceries
         FROM GroceryListEntity groceryList
         LEFT JOIN GroceryEntity grocery ON grocery.groceryListId = groceryList.id
         WHERE groceryList.id = :id
    """
    )
    fun getGroceryListById(id: String): Flow<GroceryList?>

    @Query(
        """
        SELECT 
            groceryList.id, 
            groceryList.name, 
            groceryList.sortingPriority, 
            COUNT(grocery.productId) as numOfGroceries
        FROM GroceryListEntity groceryList
        LEFT JOIN GroceryEntity grocery ON grocery.groceryListId = groceryList.id
        GROUP BY groceryList.id
    """
    )
    fun getAllGroceryLists(): Flow<List<GroceryList>>

    @Update
    suspend fun updateGroceryList(groceryList: GroceryListEntity)

    @Update
    suspend fun updateGroceryLists(groceryLists: List<GroceryListEntity>)

    @Query("DELETE FROM GroceryListEntity WHERE id = :groceryListId")
    suspend fun deleteGroceryListById(groceryListId: String)
}
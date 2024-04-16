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
         GROUP BY groceryList.id HAVING groceryList.id IS NOT NULL
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
        GROUP BY groceryList.id HAVING groceryList.id IS NOT NULL
    """
    )
    fun getAllGroceryLists(): Flow<List<GroceryList>>

    @Query("UPDATE GroceryListEntity SET name = :name WHERE id = :listId")
    suspend fun updateGroceryListName(listId: String, name: String)

    @Update
    suspend fun updateGroceryLists(groceryLists: List<GroceryListEntity>)

    @Query("DELETE FROM GroceryListEntity WHERE id = :groceryListId")
    suspend fun deleteGroceryListById(groceryListId: String)
}
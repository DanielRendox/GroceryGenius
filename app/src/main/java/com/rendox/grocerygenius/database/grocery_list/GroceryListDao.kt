package com.rendox.grocerygenius.database.grocery_list

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryListDao {
    @Upsert
    suspend fun upsertGroceryLists(groceryLists: List<GroceryListEntity>)

    @Insert
    suspend fun insertGroceryList(groceryList: GroceryListEntity)

    @Query("SELECT * FROM GroceryListEntity WHERE id = :id")
    fun getGroceryListById(id: String): Flow<GroceryListEntity?>

    @Query("SELECT * FROM GroceryListEntity")
    fun getAllGroceryLists(): Flow<List<GroceryListEntity>>

    @Update
    suspend fun updateGroceryList(groceryList: GroceryListEntity)

    @Query("DELETE FROM GroceryListEntity WHERE id = :groceryListId")
    suspend fun deleteGroceryListById(groceryListId: String)
}
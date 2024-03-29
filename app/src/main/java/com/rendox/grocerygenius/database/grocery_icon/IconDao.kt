package com.rendox.grocerygenius.database.grocery_icon

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IconDao {
    @Insert
    suspend fun insertGroceryIcons(groceryIconEntities: List<IconEntity>)

    @Query("SELECT * FROM IconEntity")
    fun getAllGroceryIcons(): Flow<List<IconEntity>>
}
package com.rendox.grocerygenius.database.category

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM CategoryEntity")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM CategoryEntity WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultCategory(): CategoryEntity?
}
package com.rendox.grocerygenius.database.grocery_icon

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface IconDao {
    @Upsert
    suspend fun upsertGroceryIcons(groceryIconEntities: List<IconEntity>)

    @Query("SELECT * FROM IconEntity")
    fun getAllGroceryIcons(): Flow<List<IconEntity>>

    @Query(
        """
            DELETE FROM IconEntity
            WHERE id in (:ids)
        """,
    )
    suspend fun deleteIcons(ids: List<String>)
}
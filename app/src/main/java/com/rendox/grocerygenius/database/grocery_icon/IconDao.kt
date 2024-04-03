package com.rendox.grocerygenius.database.grocery_icon

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rendox.grocerygenius.model.IconReference

@Dao
interface IconDao {
    @Upsert
    suspend fun upsertGroceryIcons(groceryIconEntities: List<IconEntity>)

    @Query("""
        SELECT 
        i.id,
        i.filePath,
        p.name
        FROM IconEntity i
        INNER JOIN ProductEntity p ON i.id = p.iconId
        GROUP BY i.id
    """)
    suspend fun getAllGroceryIcons(): List<IconReference>

    @Query(
        """
            DELETE FROM IconEntity
            WHERE id in (:ids)
        """,
    )
    suspend fun deleteIcons(ids: List<String>)
}
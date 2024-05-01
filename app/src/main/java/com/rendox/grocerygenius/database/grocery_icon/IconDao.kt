package com.rendox.grocerygenius.database.grocery_icon

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.rendox.grocerygenius.model.IconReference
import kotlinx.coroutines.flow.Flow

@Dao
interface IconDao {
    @Insert
    suspend fun insertGroceryIcons(groceryIconEntities: List<IconEntity>)

    @Upsert
    suspend fun upsertGroceryIcons(groceryIconEntities: List<IconEntity>)

    @Query(
        """
        SELECT 
        i.uniqueFileName,
        i.filePath,
        p.name
        FROM IconEntity i
        LEFT JOIN ProductEntity p ON i.uniqueFileName = p.iconFileName
        GROUP BY i.uniqueFileName
    """
    )
    fun getAllGroceryIcons(): Flow<List<IconReference>>

    @Query(
        """
            DELETE FROM IconEntity
            WHERE uniqueFileName in (:ids)
        """,
    )
    suspend fun deleteIcons(ids: List<String>)
}
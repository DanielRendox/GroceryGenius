package com.rendox.grocerygenius.database.grocery_icon

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.IconReference
import kotlinx.coroutines.flow.Flow

@Dao
abstract class IconDao {
    @Insert
    abstract suspend fun insertGroceryIcons(groceryIconEntities: List<IconEntity>)

    @Upsert
    abstract suspend fun upsertGroceryIcons(groceryIconEntities: List<IconEntity>)

    @Query(
        """
        SELECT 
        c.id,
        c.name,
        c.sortingPriority,
        c.defaultSortingPriority,
        i.uniqueFileName,
        i.filePath,
        p.name
        FROM IconEntity i
        INNER JOIN ProductEntity p ON i.uniqueFileName = p.iconFileName
        INNER JOIN CategoryEntity c ON p.categoryId = c.id
        WHERE p.isDefault IS 1
        GROUP BY i.uniqueFileName
    """
    )
    abstract fun getIconsGroupedByCategory(): Flow<Map<Category, List<IconReference>>>

    @Query(
        """
        SELECT 
        i.uniqueFileName,
        i.filePath,
        p.name
        FROM IconEntity i
        LEFT JOIN ProductEntity p ON i.uniqueFileName = p.iconFileName
        WHERE LOWER(p.name) LIKE LOWER(:name)
        GROUP BY i.uniqueFileName
        """
    )
    abstract suspend fun getGroceryIconsByName(name: String): List<IconReference>

    @Query(
        """
            DELETE FROM IconEntity
            WHERE uniqueFileName in (:ids)
        """,
    )
    abstract suspend fun deleteIcons(ids: List<String>)
}
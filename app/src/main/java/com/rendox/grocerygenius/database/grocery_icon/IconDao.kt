package com.rendox.grocerygenius.database.grocery_icon

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import com.rendox.grocerygenius.database.product.ProductEntity
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


    /**
     * This function returns only those icons, which have an associated product name
     * that contains one or more given keywords. The search is designed to match whole words
     * so, for example, searching for "apple" will not match "pineapple" or "applesauce". This is
     * useful when you don't want irrelevant results to be displayed. The results are sorted in the
     * order from the most relevant to the least relevant.
     */
     suspend fun getGroceryIconsByKeywords(keywords: List<String>): List<IconReference> {
        val searchCondition = keywords.joinToString(" OR ") { keyword ->
            """
            (' ' || LOWER(p.name) || ' ') LIKE LOWER('% $keyword %')
        """.trimIndent()
        }
        val orderCondition = keywords.joinToString(" + ") { keyword ->
            """
            CASE
                WHEN (' ' || LOWER(p.name) || ' ') LIKE LOWER('% $keyword %') THEN 1
                ELSE 0
            END
        """.trimIndent()
        }
        val queryString = """
            SELECT
            i.uniqueFileName,
            i.filePath,
            p.name
            FROM IconEntity i
            LEFT JOIN ProductEntity p ON i.uniqueFileName = p.iconFileName
            WHERE $searchCondition
            GROUP BY i.uniqueFileName
            ORDER BY $orderCondition DESC
        """
        return getGroceryIconsByRawQuery(SimpleSQLiteQuery(queryString))
    }

    @RawQuery(observedEntities = [IconEntity::class, ProductEntity::class])
    protected abstract suspend fun getGroceryIconsByRawQuery(
        query: SimpleSQLiteQuery
    ): List<IconReference>

    @Query(
        """
            DELETE FROM IconEntity
            WHERE uniqueFileName in (:ids)
        """,
    )
    abstract suspend fun deleteIcons(ids: List<String>)
}
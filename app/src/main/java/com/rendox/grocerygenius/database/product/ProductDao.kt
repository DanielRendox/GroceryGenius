package com.rendox.grocerygenius.database.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import com.rendox.grocerygenius.database.grocery_icon.IconEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDao {
    @Insert
    abstract suspend fun insertProduct(product: ProductEntity)

    @Insert
    abstract suspend fun insertProducts(products: List<ProductEntity>)

    @Upsert
    abstract suspend fun upsertProducts(products: List<ProductEntity>)

    @Query(
        """
        SELECT
        product.id,
        product.name,
        icon.uniqueFileName as iconId,
        icon.filePath as iconFilePath,
        category.id as categoryId,
        category.name as categoryName,
        category.sortingPriority as categorySortingPriority,
        product.isDefault
        FROM ProductEntity product
        LEFT JOIN CategoryEntity category ON product.categoryId = category.id
        LEFT JOIN IconEntity icon ON product.iconFileName = icon.uniqueFileName
        WHERE product.id = :productId
        """
    )
    abstract fun getProductById(productId: String): Flow<CombinedProduct?>

    @Query(
        """
        SELECT 
            product.id,
            product.name,
            icon.uniqueFileName as iconId,
            icon.filePath as iconFilePath,
            category.id as categoryId,
            category.name as categoryName,
            category.sortingPriority as categorySortingPriority,
            product.isDefault
        FROM ProductEntity product
        LEFT JOIN CategoryEntity category ON product.categoryId = category.id
        LEFT JOIN IconEntity icon ON product.iconFileName = icon.uniqueFileName
        WHERE product.categoryId = :categoryId
    """
    )
    protected abstract fun getProductsByCategoryId(categoryId: String): Flow<List<CombinedProduct>>

    @Query(
        """
        SELECT 
            product.id,
            product.name,
            icon.uniqueFileName as iconId,
            icon.filePath as iconFilePath,
            category.id as categoryId,
            category.name as categoryName,
            category.sortingPriority as categorySortingPriority,
            product.isDefault
        FROM ProductEntity product
        LEFT JOIN CategoryEntity category ON product.categoryId = category.id
        LEFT JOIN IconEntity icon ON product.iconFileName = icon.uniqueFileName
        WHERE product.categoryId IS NULL
    """
    )
    protected abstract fun getProductsWithoutCategory(): Flow<List<CombinedProduct>>

    fun getProductsByCategory(categoryId: String?): Flow<List<CombinedProduct>> {
        return if (categoryId == null) {
            getProductsWithoutCategory()
        } else {
            getProductsByCategoryId(categoryId)
        }
    }

    @Query(
        """
        SELECT 
            product.id,
            product.name,
            icon.uniqueFileName as iconId,
            icon.filePath as iconFilePath,
            category.id as categoryId,
            category.name as categoryName,
            category.sortingPriority as categorySortingPriority,
            product.isDefault
        FROM ProductEntity product
        LEFT JOIN CategoryEntity category ON product.categoryId = category.id
        LEFT JOIN IconEntity icon ON product.iconFileName = icon.uniqueFileName
        WHERE LOWER(product.name) LIKE LOWER(:name)
    """
    )
    abstract suspend fun getProductsByName(name: String): List<CombinedProduct>

    /**
     * This function returns only those icons, which have an associated product name
     * that contains one or more given keywords. The search is designed to match whole words
     * so, for example, searching for "apple" will not match "pineapple" or "applesauce". This is
     * useful when you don't want irrelevant results to be displayed. The results are sorted in the
     * order from the most relevant to the least relevant.
     */
    suspend fun getProductsByKeywords(keywords: List<String>): List<CombinedProduct> {
        val searchCondition = keywords.joinToString(" OR ") { keyword ->
            """
            (' ' || LOWER(product.name) || ' ') LIKE LOWER('% $keyword %')
        """.trimIndent()
        }
        val orderCondition = keywords.joinToString(" + ") { keyword ->
            """
            CASE
                WHEN (' ' || LOWER(product.name) || ' ') LIKE LOWER('% $keyword %') THEN 1
                ELSE 0
            END
        """.trimIndent()
        }
        val queryString = """
            SELECT
            product.id,
            product.name,
            icon.uniqueFileName as iconId,
            icon.filePath as iconFilePath,
            category.id as categoryId,
            category.name as categoryName,
            category.sortingPriority as categorySortingPriority,
            product.isDefault
            FROM ProductEntity product
            LEFT JOIN CategoryEntity category ON product.categoryId = category.id
            LEFT JOIN IconEntity icon ON product.iconFileName = icon.uniqueFileName
            WHERE $searchCondition
            GROUP BY icon.uniqueFileName
            ORDER BY $orderCondition DESC, LENGTH(product.name) ASC
        """
        return getProductsByRawQuery(SimpleSQLiteQuery(queryString))
    }

    @RawQuery(observedEntities = [IconEntity::class, ProductEntity::class])
    protected abstract suspend fun getProductsByRawQuery(
        query: SimpleSQLiteQuery
    ): List<CombinedProduct>

    @Query("UPDATE ProductEntity SET categoryId = :categoryId WHERE id = :productId")
    abstract suspend fun updateProductCategory(productId: String, categoryId: String?)

    @Query("UPDATE ProductEntity SET iconFileName = :iconId WHERE id = :productId")
    abstract suspend fun updateProductIcon(productId: String, iconId: String?)

    @Query("DELETE FROM ProductEntity WHERE id = :productId")
    abstract suspend fun deleteProductById(productId: String)

    @Query(
        """
            DELETE FROM ProductEntity
            WHERE id in (:ids)
        """,
    )
    abstract suspend fun deleteProductsByIds(ids: List<String>)
}
package com.rendox.grocerygenius.database.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
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

    @Query("SELECT * FROM ProductEntity")
    abstract suspend fun getAllProducts(): List<ProductEntity>

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
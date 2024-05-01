package com.rendox.grocerygenius.database.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ProductDao {
    @Insert
    suspend fun insertProduct(product: ProductEntity)

    @Insert
    suspend fun insertProducts(products: List<ProductEntity>)

    @Upsert
    suspend fun upsertProducts(products: List<ProductEntity>)

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
    suspend fun getProductsByCategory(categoryId: String): List<CombinedProduct>

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
    suspend fun getProductsByName(name: String): List<CombinedProduct>

    @Query("SELECT * FROM ProductEntity")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("UPDATE ProductEntity SET categoryId = :categoryId WHERE id = :productId")
    suspend fun updateProductCategory(productId: String, categoryId: String?)

    @Query("UPDATE ProductEntity SET iconFileName = :iconId WHERE id = :productId")
    suspend fun updateProductIcon(productId: String, iconId: String)

    @Query("DELETE FROM ProductEntity WHERE id = :productId")
    suspend fun deleteProductById(productId: String)

    @Query(
        """
            DELETE FROM ProductEntity
            WHERE id in (:ids)
        """,
    )
    suspend fun deleteProductsByIds(ids: List<String>)
}
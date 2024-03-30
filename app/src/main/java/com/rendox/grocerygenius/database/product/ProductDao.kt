package com.rendox.grocerygenius.database.product

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface ProductDao {
    @Insert
    suspend fun insertProduct(product: ProductEntity)

    @Upsert
    suspend fun upsertProducts(products: List<ProductEntity>)

    @Query(
        """
        SELECT 
            product.id,
            product.name,
            icon.id as iconId,
            icon.filePath as iconFilePath,
            category.id as categoryId,
            category.name as categoryName,
            category.sortingPriority as categorySortingPriority,
            product.deletable
        FROM ProductEntity product
        LEFT JOIN CategoryEntity category ON product.categoryId = category.id
        LEFT JOIN IconEntity icon ON product.iconId = icon.id
        WHERE product.categoryId = :categoryId
    """
    )
    suspend fun getProductsByCategory(categoryId: Int): List<CombinedProduct>

    @Query(
        """
        SELECT 
            product.id,
            product.name,
            icon.id as iconId,
            icon.filePath as iconFilePath,
            category.id as categoryId,
            category.name as categoryName,
            category.sortingPriority as categorySortingPriority,
            product.deletable
        FROM ProductEntity product
        LEFT JOIN CategoryEntity category ON product.categoryId = category.id
        LEFT JOIN IconEntity icon ON product.iconId = icon.id
        WHERE LOWER(product.name) LIKE LOWER(:name)
    """
    )
    suspend fun getProductsByName(name: String): List<CombinedProduct>

    @Query("SELECT * FROM ProductEntity")
    suspend fun getAllProducts(): List<ProductEntity>

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query(
        """
            DELETE FROM ProductEntity
            WHERE id in (:ids)
        """,
    )
    suspend fun deleteProductsByIds(ids: List<Int>)
}
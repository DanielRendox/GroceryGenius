package com.rendox.grocerygenius.database.product

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {
    @Insert
    suspend fun insertProduct(product: ProductEntity)

    @Insert
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query(
        """
        SELECT 
            product.id,
            product.name,
            icon.filePath,
            category.id as categoryId,
            category.name as categoryName,
            category.sortingPriority as categorySortingPriority,
            product.deletable
        FROM ProductEntity product
        LEFT JOIN CategoryEntity category ON product.categoryId = category.id
        LEFT JOIN GroceryIconEntity icon ON product.iconId = icon.id
        WHERE product.categoryId = :categoryId
    """
    )
    suspend fun getProductsByCategory(categoryId: Int): List<CombinedProduct>

    @Query(
        """
        SELECT 
            product.id,
            product.name,
            icon.id,
            icon.filePath,
            category.id as categoryId,
            category.name as categoryName,
            category.sortingPriority as categorySortingPriority,
            product.deletable
        FROM ProductEntity product
        LEFT JOIN CategoryEntity category ON product.categoryId = category.id
        LEFT JOIN GroceryIconEntity icon ON product.iconId = icon.id
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
}
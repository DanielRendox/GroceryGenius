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
        SELECT *
        FROM ProductEntity product
        WHERE product.categoryId = :categoryId
    """
    )
    suspend fun getProductsByCategory(categoryId: Int): List<ProductEntity>

    @Query(
        """
        SELECT *
        FROM ProductEntity product
        WHERE LOWER(product.name) LIKE LOWER(:name)
    """
    )
    suspend fun getProductsByName(name: String): List<ProductEntity>

    @Query("SELECT * FROM ProductEntity")
    suspend fun getAllProducts(): List<ProductEntity>

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}
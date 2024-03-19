package com.rendox.grocerygenius.database.product

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rendox.grocerygenius.model.Product

@Dao
interface ProductDao {
    @Insert
    suspend fun insertProduct(product: ProductEntity)

    @Query("""
        SELECT product.id, product.name, product.iconUri, category.id as categoryId, product.deletable
        FROM ProductEntity product, CategoryEntity category
        WHERE categoryId = :categoryId
    """)
    suspend fun getProductsByCategory(categoryId: Int): List<Product>

    @Query("""
        SELECT product.id, product.name, product.iconUri, category.id as categoryId, product.deletable
        FROM ProductEntity product
        JOIN CategoryEntity category ON product.categoryId = category.id
        WHERE LOWER(product.name) LIKE LOWER(:name)
    """)
    suspend fun getProductsByName(name: String): List<Product>

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}
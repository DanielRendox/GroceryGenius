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
            product.iconUri,
            category.id as categoryId,
            category.name as categoryName,
            category.iconUri as categoryIconUri,
            category.sortingPriority as categorySortingPriority,
            category.isDefault as categoryIsDefault,
            product.deletable
        FROM ProductEntity product
        INNER JOIN CategoryEntity category ON product.categoryId = category.id
        WHERE product.categoryId = :categoryId
    """
    )
    suspend fun getProductsByCategory(categoryId: Int): List<CombinedProduct>

    @Query(
        """
        SELECT 
            product.id,
            product.name,
            product.iconUri,
            category.id as categoryId,
            category.name as categoryName,
            category.iconUri as categoryIconUri,
            category.sortingPriority as categorySortingPriority,
            category.isDefault as categoryIsDefault,
            product.deletable
        FROM ProductEntity product
        INNER JOIN CategoryEntity category ON product.categoryId = category.id
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
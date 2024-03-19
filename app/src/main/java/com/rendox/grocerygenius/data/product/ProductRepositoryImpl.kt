package com.rendox.grocerygenius.data.product

import com.rendox.grocerygenius.data.model.asEntity
import com.rendox.grocerygenius.database.product.ProductDao
import com.rendox.grocerygenius.model.Product
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
) : ProductRepository {
    override suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product.asEntity())
    }

    override suspend fun getProductsByCategory(categoryId: Int): List<Product> {
        return productDao.getProductsByCategory(categoryId)
    }

    override suspend fun getProductsByName(name: String): List<Product> {
        return productDao.getProductsByName(name)
    }

    override suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product.asEntity())
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product.asEntity())
    }
}
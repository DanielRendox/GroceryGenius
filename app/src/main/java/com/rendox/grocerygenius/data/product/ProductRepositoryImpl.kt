package com.rendox.grocerygenius.data.product

import com.rendox.grocerygenius.data.Synchronizer
import com.rendox.grocerygenius.data.model.asEntity
import com.rendox.grocerygenius.data.model.asExternalModel
import com.rendox.grocerygenius.database.product.ProductDao
import com.rendox.grocerygenius.file_storage.AssetToFileSaver
import com.rendox.grocerygenius.file_storage.BitmapLoader
import com.rendox.grocerygenius.model.Product
import com.rendox.grocerygenius.network.product.ProductNetworkDataSource
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val productNetworkDataSource: ProductNetworkDataSource,
    private val bitmapLoader: BitmapLoader,
) : ProductRepository {
    override suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product.asEntity())
    }

    override suspend fun getProductsByCategory(categoryId: Int): List<Product> {
        return productDao.getProductsByCategory(categoryId).map { combinedProduct ->
            val productIcon = combinedProduct.iconFilePath?.let { bitmapLoader.loadFromFile(it) }
            combinedProduct.asExternalModel(productIcon)
        }
    }

    override suspend fun getProductsByName(name: String): List<Product> {
        return productDao.getProductsByName(name).map { combinedProduct ->
            val productIcon = combinedProduct.iconFilePath?.let { bitmapLoader.loadFromFile(it) }
            combinedProduct.asExternalModel(productIcon)
        }
    }

    override suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product.asEntity())
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product.asEntity())
    }

    override suspend fun syncWith(synchronizer: Synchronizer) {
        val existingProducts = productDao.getAllProducts()
        if (existingProducts.isEmpty()) {
            val products = productNetworkDataSource.getAllProducts()
            productDao.insertProducts(products.map { it.asEntity() })
        }
    }
}
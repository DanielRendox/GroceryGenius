package com.rendox.grocerygenius.data.product

import com.rendox.grocerygenius.data.Synchronizer
import com.rendox.grocerygenius.data.changeListSync
import com.rendox.grocerygenius.data.model.asEntity
import com.rendox.grocerygenius.data.model.asExternalModel
import com.rendox.grocerygenius.database.product.ProductDao
import com.rendox.grocerygenius.model.Product
import com.rendox.grocerygenius.network.product.ProductNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val productNetworkDataSource: ProductNetworkDataSource,
) : ProductRepository {
    override suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product.asEntity())
    }

    override fun getProductById(productId: String): Flow<Product?> {
        return productDao.getProductById(productId).map { it?.asExternalModel() }
    }

    override fun getProductsByCategory(categoryId: String?): Flow<List<Product>> {
        return productDao.getProductsByCategory(categoryId).map { products ->
            products.map { it.asExternalModel() }
        }
    }

    override suspend fun getProductsByName(name: String): List<Product> {
        return productDao.getProductsByName(name).map { combinedProduct ->
            combinedProduct.asExternalModel()
        }
    }

    override suspend fun updateProductCategory(productId: String, categoryId: String?) {
        productDao.updateProductCategory(productId, categoryId)
    }

    override suspend fun updateProductIcon(productId: String, iconId: String?) {
        productDao.updateProductIcon(productId, iconId)
    }

    override suspend fun deleteProductById(productId: String) {
        productDao.deleteProductById(productId)
    }

    override suspend fun syncWith(synchronizer: Synchronizer) = synchronizer.changeListSync(
        prepopulateWithInitialData = {
            val products = productNetworkDataSource.getAllProducts()
            productDao.insertProducts(products.map { it.asEntity() })
        },
        versionReader = { it.productVersion },
        changeListFetcher = { currentVersion ->
            productNetworkDataSource.getProductChangeList(after = currentVersion)
        },
        versionUpdater = { latestVersion ->
            copy(productVersion = latestVersion)
        },
        modelDeleter = { productIds ->
            productDao.deleteProductsByIds(productIds)
        },
        modelUpdater = { changedIds ->
            val networkCategories = productNetworkDataSource.getProductsByIds(ids = changedIds)
            productDao.upsertProducts(networkCategories.map { it.asEntity() })
        },
    )
}
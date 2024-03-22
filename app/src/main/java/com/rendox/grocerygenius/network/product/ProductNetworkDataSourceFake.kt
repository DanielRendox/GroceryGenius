package com.rendox.grocerygenius.network.product

import com.rendox.grocerygenius.model.Product
import com.rendox.grocerygenius.network.sampleProductList
import javax.inject.Inject

class ProductNetworkDataSourceFake @Inject constructor(): ProductNetworkDataSource {

    override suspend fun getAllProducts(): List<Product> = sampleProductList
}

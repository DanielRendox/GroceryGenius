package com.rendox.grocerygenius.network.product

import com.rendox.grocerygenius.network.model.ProductNetwork
import javax.inject.Inject

class ProductNetworkDataSourceFake @Inject constructor(): ProductNetworkDataSource {

    override suspend fun getAllProducts(): List<ProductNetwork> = emptyList()
}

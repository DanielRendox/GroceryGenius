package com.rendox.grocerygenius.network.product

import com.rendox.grocerygenius.model.Product
import com.rendox.grocerygenius.network.model.ProductNetwork

interface ProductNetworkDataSource {
    suspend fun getAllProducts(): List<ProductNetwork>
}
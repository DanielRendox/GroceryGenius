package com.rendox.grocerygenius.network.product

import com.rendox.grocerygenius.model.Product

interface ProductNetworkDataSource {
    suspend fun getAllProducts(): List<Product>
}
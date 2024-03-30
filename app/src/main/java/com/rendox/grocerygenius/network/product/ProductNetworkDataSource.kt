package com.rendox.grocerygenius.network.product

import com.rendox.grocerygenius.network.model.NetworkChangeList
import com.rendox.grocerygenius.network.model.ProductNetwork

interface ProductNetworkDataSource {
    suspend fun getAllProducts(): List<ProductNetwork>
    suspend fun getProductsByIds(ids: List<Int>): List<ProductNetwork>
    suspend fun getProductChangeList(after: Int): List<NetworkChangeList>
}
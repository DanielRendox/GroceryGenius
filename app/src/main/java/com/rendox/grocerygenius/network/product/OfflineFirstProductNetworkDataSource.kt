package com.rendox.grocerygenius.network.product

import com.rendox.grocerygenius.network.GitHubApi
import com.rendox.grocerygenius.network.model.NetworkChangeList
import com.rendox.grocerygenius.network.model.ProductNetwork
import javax.inject.Inject

class OfflineFirstProductNetworkDataSource @Inject constructor(
    private val gitHubApi: GitHubApi,
): ProductNetworkDataSource {
    override suspend fun getAllProducts(): List<ProductNetwork> =
        gitHubApi.getProducts()

    override suspend fun getProductsByIds(ids: List<String>): List<ProductNetwork> =
        gitHubApi.getProducts().filter { it.id in ids }

    override suspend fun getProductChangeList(after: Int): List<NetworkChangeList> =
        gitHubApi.getProductsChangeList().filter { it.changeListVersion > after}
}
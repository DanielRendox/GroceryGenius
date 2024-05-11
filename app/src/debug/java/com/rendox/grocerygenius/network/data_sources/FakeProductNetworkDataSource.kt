package com.rendox.grocerygenius.network.data_sources

import com.rendox.grocerygenius.file_storage.JsonAssetDecoder
import com.rendox.grocerygenius.network.listAdapter
import com.rendox.grocerygenius.network.model.NetworkChangeList
import com.rendox.grocerygenius.network.model.ProductNetwork
import com.squareup.moshi.Moshi
import javax.inject.Inject

class FakeProductNetworkDataSource @Inject constructor(
    private val jsonAssetDecoder: JsonAssetDecoder,
    private val moshi: Moshi,
): ProductNetworkDataSource {

    override suspend fun getAllProducts(): List<ProductNetwork> {
        return jsonAssetDecoder.decodeFromFile(
            adapter = moshi.listAdapter<ProductNetwork>(),
            fileName = "product/default_products.json",
        ) ?: emptyList()
    }

    override suspend fun getProductsByIds(ids: List<String>): List<ProductNetwork> =
        getAllProducts().filter { it.id in ids }

    override suspend fun getProductChangeList(after: Int): List<NetworkChangeList> {
        return jsonAssetDecoder.decodeFromFile(
            adapter = moshi.listAdapter<NetworkChangeList>(),
            fileName = "product/default_products_change_list.json",
        )?.filter { it.changeListVersion > after } ?: emptyList()
    }
}

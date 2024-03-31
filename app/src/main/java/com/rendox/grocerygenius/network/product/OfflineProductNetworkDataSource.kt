package com.rendox.grocerygenius.network.product

import com.rendox.grocerygenius.file_storage.JsonAssetDecoder
import com.rendox.grocerygenius.network.model.NetworkChangeList
import com.rendox.grocerygenius.network.model.ProductNetwork
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

class OfflineProductNetworkDataSource @Inject constructor(
    private val jsonAssetDecoder: JsonAssetDecoder,
    private val moshi: Moshi,
): ProductNetworkDataSource {

    override suspend fun getAllProducts(): List<ProductNetwork> {
        val type = Types.newParameterizedType(List::class.java, ProductNetwork::class.java)
        val adapter = moshi.adapter<List<ProductNetwork>>(type)
        return jsonAssetDecoder.decodeFromFile(
            adapter = adapter,
            fileName = "product/default_products.json",
        ) ?: emptyList()
    }

    override suspend fun getProductsByIds(ids: List<String>): List<ProductNetwork> =
        getAllProducts().filter { it.id in ids }

    override suspend fun getProductChangeList(after: Int): List<NetworkChangeList> {
        val type = Types.newParameterizedType(List::class.java, NetworkChangeList::class.java)
        val adapter = moshi.adapter<List<NetworkChangeList>>(type)
        return jsonAssetDecoder.decodeFromFile(
            adapter = adapter,
            fileName = "product/default_products_change_list.json",
        )?.filter { it.changeListVersion > after } ?: emptyList()
    }
}

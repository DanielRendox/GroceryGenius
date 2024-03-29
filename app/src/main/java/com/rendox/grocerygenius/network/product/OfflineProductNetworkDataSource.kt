package com.rendox.grocerygenius.network.product

import com.rendox.grocerygenius.file_storage.JsonAssetDecoder
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
            fileName = "default_products.json",
        ) ?: emptyList()
    }
}

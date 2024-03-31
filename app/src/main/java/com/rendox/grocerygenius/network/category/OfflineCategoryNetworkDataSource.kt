package com.rendox.grocerygenius.network.category

import com.rendox.grocerygenius.file_storage.JsonAssetDecoder
import com.rendox.grocerygenius.network.model.CategoryNetwork
import com.rendox.grocerygenius.network.model.NetworkChangeList
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

class OfflineCategoryNetworkDataSource @Inject constructor(
    private val jsonAssetDecoder: JsonAssetDecoder,
    private val moshi: Moshi,
) : CategoryNetworkDataSource {
    override suspend fun getAllCategories(): List<CategoryNetwork> {
        val type = Types.newParameterizedType(List::class.java, CategoryNetwork::class.java)
        val adapter = moshi.adapter<List<CategoryNetwork>>(type)
        return jsonAssetDecoder.decodeFromFile(
            adapter = adapter,
            fileName = "category/categories.json",
        ) ?: emptyList()
    }

    override suspend fun getCategoriesByIds(ids: List<String>): List<CategoryNetwork> {
        return getAllCategories().filter { it.id in ids }
    }

    override suspend fun getCategoryChangeList(after: Int): List<NetworkChangeList> {
        val type = Types.newParameterizedType(List::class.java, NetworkChangeList::class.java)
        val adapter = moshi.adapter<List<NetworkChangeList>>(type)
        return jsonAssetDecoder.decodeFromFile(
            adapter = adapter,
            fileName = "category/categories_change_list.json",
        )?.filter { it.changeListVersion > after } ?: emptyList()
    }
}
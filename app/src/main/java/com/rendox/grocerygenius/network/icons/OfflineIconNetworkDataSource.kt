package com.rendox.grocerygenius.network.icons

import com.rendox.grocerygenius.file_storage.AssetToFileSaver
import com.rendox.grocerygenius.file_storage.JsonAssetDecoder
import com.rendox.grocerygenius.model.Icon
import com.rendox.grocerygenius.network.model.GroceryIconAsset
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

class OfflineIconNetworkDataSource @Inject constructor(
    private val jsonAssetDecoder: JsonAssetDecoder,
    private val moshi: Moshi,
    private val assetToFileSaver: AssetToFileSaver,
) : IconNetworkDataSource {
    override suspend fun downloadIcons(): List<Icon> {
        val type = Types.newParameterizedType(List::class.java, GroceryIconAsset::class.java)
        val adapter = moshi.adapter<List<GroceryIconAsset>>(type)
        val icons = jsonAssetDecoder.decodeFromFile(
            adapter = adapter,
            fileName = "icons.json",
        ) ?: return emptyList()
        return icons.mapNotNull { icon ->
            val file = assetToFileSaver.copyAssetToInternalStorage(icon.assetFilePath)
            file?.let {
                Icon(
                    id = icon.id,
                    filePath = it.absolutePath,
                )
            }
        }
    }
}
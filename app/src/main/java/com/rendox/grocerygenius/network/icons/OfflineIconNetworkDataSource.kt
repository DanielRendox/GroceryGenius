package com.rendox.grocerygenius.network.icons

import android.content.Context
import com.rendox.grocerygenius.file_storage.AssetToFileSaver
import com.rendox.grocerygenius.file_storage.JsonAssetDecoder
import com.rendox.grocerygenius.model.IconReference
import com.rendox.grocerygenius.network.model.GroceryIconAsset
import com.rendox.grocerygenius.network.model.NetworkChangeList
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OfflineIconNetworkDataSource @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val jsonAssetDecoder: JsonAssetDecoder,
    private val moshi: Moshi,
    private val assetToFileSaver: AssetToFileSaver,
) : IconNetworkDataSource {

    override suspend fun downloadIcons(): List<IconReference> =
        getReferencesToIcons().saveIconsToInternalStorage()

    override suspend fun downloadIconsByIds(ids: List<String>): List<IconReference> =
        getReferencesToIcons().filter { it.id in ids }.saveIconsToInternalStorage()

    private suspend fun getReferencesToIcons(): List<GroceryIconAsset> {
        val type = Types.newParameterizedType(List::class.java, GroceryIconAsset::class.java)
        val adapter = moshi.adapter<List<GroceryIconAsset>>(type)
        return jsonAssetDecoder.decodeFromFile(
            adapter = adapter,
            fileName = "icon/icons.json",
        ) ?: emptyList()
    }

    private suspend fun List<GroceryIconAsset>.saveIconsToInternalStorage(): List<IconReference> =
        this.mapNotNull { icon ->
            val file = assetToFileSaver.copyAssetToInternalStorage(icon.assetFilePath)
            file?.let {
                IconReference(
                    id = icon.id,
                    filePath = it.toRelativeString(appContext.filesDir),
                )
            }
        }

    override suspend fun getIconChangeList(after: Int): List<NetworkChangeList> {
        val type = Types.newParameterizedType(List::class.java, NetworkChangeList::class.java)
        val adapter = moshi.adapter<List<NetworkChangeList>>(type)
        return jsonAssetDecoder.decodeFromFile(
            adapter = adapter,
            fileName = "icon/icons_change_list.json",
        )?.filter { it.changeListVersion > after } ?: emptyList()
    }
}
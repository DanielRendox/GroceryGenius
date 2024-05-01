package com.rendox.grocerygenius.network.icons

import android.content.Context
import android.util.Log
import com.rendox.grocerygenius.file_storage.AssetToFileSaver
import com.rendox.grocerygenius.file_storage.JsonAssetDecoder
import com.rendox.grocerygenius.file_storage.UnzipUtils
import com.rendox.grocerygenius.model.IconReference
import com.rendox.grocerygenius.network.di.Dispatcher
import com.rendox.grocerygenius.network.di.GroceryGeniusDispatchers
import com.rendox.grocerygenius.network.model.NetworkChangeList
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineIconNetworkDataSource @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val jsonAssetDecoder: JsonAssetDecoder,
    private val moshi: Moshi,
    private val assetToFileSaver: AssetToFileSaver,
    @Dispatcher(GroceryGeniusDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : IconNetworkDataSource {

    override suspend fun downloadIcons(): List<IconReference> = withContext(ioDispatcher) {
        val iconsArchive =
            assetToFileSaver.copyAssetToInternalStorage("grocery/icons/all_icons.zip")
        if (iconsArchive == null || !iconsArchive.exists()) return@withContext emptyList()
        val extractedFiles = UnzipUtils.unzip(
            iconsArchive, appContext.filesDir.absolutePath + "/grocery/icons"
        )
            .onEach {
                println("OfflineNetworkDataSource extracted file: $it")
                println("OfflineNetworkDataSource extracted file parent: ${it.parentFile}")
            }
            .map { file ->
                IconReference(
                    uniqueFileName = file.name,
                    filePath = file.toRelativeString(appContext.filesDir),
                )
            }
        try {
            iconsArchive.delete()
        } catch (e: Exception) {
            Log.w("NetworkDataSource", "Failed to delete icons archive: $iconsArchive")
        }
        extractedFiles
    }

    override suspend fun downloadIconsByIds(
        ids: List<String>
    ): List<IconReference> = ids.mapNotNull { fileName ->
        val assetFilePath = "grocery/icons/$fileName"
        val file = assetToFileSaver.copyAssetToInternalStorage(assetFilePath)
        file?.let {
            IconReference(
                uniqueFileName = it.name,
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
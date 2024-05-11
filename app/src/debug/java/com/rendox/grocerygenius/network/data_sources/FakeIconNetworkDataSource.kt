package com.rendox.grocerygenius.network.data_sources

import android.content.Context
import android.util.Log
import com.rendox.grocerygenius.file_storage.AssetToFileSaver
import com.rendox.grocerygenius.file_storage.JsonAssetDecoder
import com.rendox.grocerygenius.file_storage.UnzipUtils
import com.rendox.grocerygenius.model.IconReference
import com.rendox.grocerygenius.network.di.Dispatcher
import com.rendox.grocerygenius.network.di.GroceryGeniusDispatchers
import com.rendox.grocerygenius.network.listAdapter
import com.rendox.grocerygenius.network.model.NetworkChangeList
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FakeIconNetworkDataSource @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val jsonAssetDecoder: JsonAssetDecoder,
    private val moshi: Moshi,
    private val assetToFileSaver: AssetToFileSaver,
    @Dispatcher(GroceryGeniusDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : IconNetworkDataSource {

    override suspend fun downloadIcons(): List<IconReference> = withContext(ioDispatcher) {
        val iconsArchive =
            assetToFileSaver.copyAssetToInternalStorage("icons/all_icons_v1.zip")
        if (iconsArchive == null) {
            throw IllegalStateException(
                "Failed to copy icons archive to internal storage because iconsArchive is null"
            )
        } else if (!iconsArchive.exists()) {
            throw IllegalStateException(
                "Failed to copy icons archive to internal storage because iconsArchive does not exist"
            )
        }
        val extractedFiles = UnzipUtils.unzip(
            iconsArchive, appContext.filesDir.absolutePath + "/icons"
        ).map { file ->
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
        val assetFilePath = "icons/$fileName"
        val file = assetToFileSaver.copyAssetToInternalStorage(assetFilePath)
        file?.let {
            IconReference(
                uniqueFileName = it.name,
                filePath = it.toRelativeString(appContext.filesDir),
            )
        }
    }

    override suspend fun getIconChangeList(after: Int): List<NetworkChangeList> {
        return jsonAssetDecoder.decodeFromFile(
            adapter = moshi.listAdapter<NetworkChangeList>(),
            fileName = "icons/icons_change_list.json",
        )?.filter { it.changeListVersion > after } ?: emptyList()
    }
}
package com.rendox.grocerygenius.file_storage

import android.content.Context
import android.net.Uri
import android.util.Log
import com.rendox.grocerygenius.network.di.Dispatcher
import com.rendox.grocerygenius.network.di.GroceryGeniusDispatchers
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class AssetToFileSaver @Inject constructor(
    @ApplicationContext private val appContext: Context,
    @Dispatcher(GroceryGeniusDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun copyAssetToInternalStorage(
        assetFilePath: String,
        outputDirPath: String? = null,
        outputFileName: String? = null,
    ): File? = withContext(ioDispatcher) {
        val derivedOutputFileName = outputFileName ?: Uri.parse(assetFilePath).lastPathSegment
        if (derivedOutputFileName == null) {
            Log.w(
                "AssetToFileSaver",
                "Failed to derive output file name from asset file name: $assetFilePath"
            )
            return@withContext null
        }
        val derivedOutputDirPath = outputDirPath ?: Uri.parse(assetFilePath)
            .pathSegments
            .dropLast(1)
            .joinToString("/")
        val outputDir = File(appContext.filesDir, derivedOutputDirPath)
        if (!outputDir.mkdirs()) {
            Log.w("AssetToFileSaver", "Failed to create directory: ${outputDir.toURI()}")
        }
        val outputFile = File(outputDir, derivedOutputFileName)
        val assetManager = appContext.assets

        try {
            val inputStream = assetManager.open(assetFilePath)
            val outputStream = FileOutputStream(outputFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Log.i("AssetToFileSaver", "File saved successfully: ${outputFile.toURI()}")
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext null
        }
        outputFile
    }
}
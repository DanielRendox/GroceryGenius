package com.rendox.grocerygenius.file_storage

import android.content.Context
import android.util.Log
import com.rendox.grocerygenius.network.di.Dispatcher
import com.rendox.grocerygenius.network.di.GroceryGeniusDispatchers
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class JsonAssetDecoder @Inject constructor(
    @ApplicationContext val appContext: Context,
    @Dispatcher(GroceryGeniusDispatchers.IO) val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun <T> decodeFromFile(
        adapter: JsonAdapter<T>,
        fileName: String,
    ): T? = withContext(ioDispatcher) {
        try {
            val json = appContext.assets
                .open(fileName)
                .bufferedReader()
                .use { it.readText() }
            adapter.fromJson(json)
        } catch (e: IOException) {
            Log.e("JsonAssetDecoder", "Error reading $fileName: ${e.message}")
            null
        } catch (e: JsonDataException) {
            Log.e("JsonAssetDecoder", "Error parsing $fileName: ${e.message}")
            null
        }
    }
}
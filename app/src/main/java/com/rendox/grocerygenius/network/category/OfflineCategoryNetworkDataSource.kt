package com.rendox.grocerygenius.network.category

import android.content.Context
import android.util.Log
import com.rendox.grocerygenius.network.di.Dispatcher
import com.rendox.grocerygenius.network.di.GroceryGeniusDispatchers
import com.rendox.grocerygenius.network.model.CategoryNetwork
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class OfflineCategoryNetworkDataSource @Inject constructor(
    @ApplicationContext private val appContext: Context,
    @Dispatcher(GroceryGeniusDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val moshi: Moshi,
) : CategoryNetworkDataSource {
    override suspend fun getAllCategories(): List<CategoryNetwork> = withContext(ioDispatcher) {
        val type = Types.newParameterizedType(List::class.java, CategoryNetwork::class.java)
        val adapter = moshi.adapter<List<CategoryNetwork>>(type)
        val categoriesFileName = "categories.json"
        val json = appContext.assets
            .open(categoriesFileName)
            .bufferedReader()
            .use { it.readText() }
        try {
            adapter.fromJson(json) ?: emptyList()
        } catch (e: IOException) {
            Log.e("CategoryNetworkDataSrc", "Error reading $categoriesFileName: ${e.message}")
            emptyList()
        } catch (e: JsonDataException) {
            Log.e("CategoryNetworkDataSrc", "Error parsing $categoriesFileName: ${e.message}")
            emptyList()
        }
    }
}
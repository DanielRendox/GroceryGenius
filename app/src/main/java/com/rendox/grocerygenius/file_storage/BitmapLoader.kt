package com.rendox.grocerygenius.file_storage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.rendox.grocerygenius.network.di.Dispatcher
import com.rendox.grocerygenius.network.di.GroceryGeniusDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BitmapLoader @Inject constructor (
    @Dispatcher(GroceryGeniusDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun loadFromFile(filePath: String): Bitmap? = withContext(ioDispatcher) {
        BitmapFactory.decodeFile(filePath)
    }
}
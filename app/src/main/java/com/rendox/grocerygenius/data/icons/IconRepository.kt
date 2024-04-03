package com.rendox.grocerygenius.data.icons

import com.rendox.grocerygenius.data.Syncable
import com.rendox.grocerygenius.model.IconReference
import kotlinx.coroutines.flow.Flow

interface IconRepository : Syncable {
    fun getAllGroceryIcons(): Flow<List<IconReference>>
}
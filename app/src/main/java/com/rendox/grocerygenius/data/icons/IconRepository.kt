package com.rendox.grocerygenius.data.icons

import com.rendox.grocerygenius.data.Syncable
import com.rendox.grocerygenius.model.IconReference

interface IconRepository : Syncable {
    suspend fun getAllGroceryIcons(): List<IconReference>
}
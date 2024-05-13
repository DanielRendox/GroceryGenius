package com.rendox.grocerygenius.data.icons

import com.rendox.grocerygenius.data.Syncable
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.IconReference
import kotlinx.coroutines.flow.Flow

interface IconRepository : Syncable {
    fun getIconsGroupedByCategory(): Flow<Map<Category, List<IconReference>>>
    suspend fun getGroceryIconsByName(name: String): List<IconReference>
}
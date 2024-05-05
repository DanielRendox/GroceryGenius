package com.rendox.grocerygenius.data.category

import com.rendox.grocerygenius.data.Syncable
import com.rendox.grocerygenius.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository : Syncable {
    fun getAllCategories(): Flow<List<Category>>
    fun getCategoryById(id: String): Flow<Category?>
    suspend fun updateCategories(categories: List<Category>)
}
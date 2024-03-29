package com.rendox.grocerygenius.data.category

import com.rendox.grocerygenius.data.Syncable
import com.rendox.grocerygenius.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository : Syncable {
    suspend fun insertCategories(categories: List<Category>)
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getDefaultCategory(): Category?
}
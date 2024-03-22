package com.rendox.grocerygenius.network.category

import com.rendox.grocerygenius.model.Category

interface CategoryNetworkDataSource {
    suspend fun getAllCategories(): List<Category>
}
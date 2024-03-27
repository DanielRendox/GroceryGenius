package com.rendox.grocerygenius.network.category

import com.rendox.grocerygenius.network.model.CategoryNetwork

interface CategoryNetworkDataSource {
    suspend fun getAllCategories(): List<CategoryNetwork>
}
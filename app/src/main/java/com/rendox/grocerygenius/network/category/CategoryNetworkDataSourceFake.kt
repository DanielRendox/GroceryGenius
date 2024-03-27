package com.rendox.grocerygenius.network.category

import com.rendox.grocerygenius.network.model.CategoryNetwork
import javax.inject.Inject

/**
 * Implementation that provides static data to aid development
 */
class CategoryNetworkDataSourceFake @Inject constructor() : CategoryNetworkDataSource {
    override suspend fun getAllCategories(): List<CategoryNetwork> = emptyList()
}
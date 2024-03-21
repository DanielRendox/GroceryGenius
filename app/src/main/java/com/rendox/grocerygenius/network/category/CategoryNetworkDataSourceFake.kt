package com.rendox.grocerygenius.network.category

import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.network.sampleCategoryList
import javax.inject.Inject

/**
 * Implementation that provides static data to aid development
 */
class CategoryNetworkDataSourceFake @Inject constructor() : CategoryNetworkDataSource {
    override suspend fun getAllCategories(): List<Category> = sampleCategoryList
}
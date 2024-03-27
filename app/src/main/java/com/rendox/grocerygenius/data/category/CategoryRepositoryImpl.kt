package com.rendox.grocerygenius.data.category

import com.rendox.grocerygenius.data.Synchronizer
import com.rendox.grocerygenius.data.model.asEntity
import com.rendox.grocerygenius.data.model.asExternalModel
import com.rendox.grocerygenius.database.category.CategoryDao
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.network.category.CategoryNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categoryNetworkDataSource: CategoryNetworkDataSource,
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { categories ->
            categories.map { categoryEntity ->
                categoryEntity.asExternalModel()
            }
        }
    }

    override suspend fun syncWith(synchronizer: Synchronizer) {
        val existingCategories = categoryDao.getAllCategories().first()
        if (existingCategories.isEmpty()) {
            val categories = categoryNetworkDataSource.getAllCategories()
            categoryDao.insertCategories(categories.map { it.asEntity() })
        }
    }
}
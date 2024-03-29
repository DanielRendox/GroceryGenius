package com.rendox.grocerygenius.data.icons

import com.rendox.grocerygenius.data.Synchronizer
import com.rendox.grocerygenius.data.model.asEntity
import com.rendox.grocerygenius.database.grocery_icon.IconDao
import com.rendox.grocerygenius.network.icons.IconNetworkDataSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IconRepositoryImpl @Inject constructor(
    private val iconDao: IconDao,
    private val iconNetworkDataSource: IconNetworkDataSource,
) : IconRepository {
    override suspend fun syncWith(synchronizer: Synchronizer) {
        val existingIcons = iconDao.getAllGroceryIcons().first()
        if (existingIcons.isEmpty()) {
            val icons = iconNetworkDataSource.downloadIcons()
            iconDao.insertGroceryIcons(icons.map { it.asEntity() })
        }
    }
}
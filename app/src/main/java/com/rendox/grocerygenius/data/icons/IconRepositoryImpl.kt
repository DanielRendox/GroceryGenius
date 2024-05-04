package com.rendox.grocerygenius.data.icons

import com.rendox.grocerygenius.data.Synchronizer
import com.rendox.grocerygenius.data.changeListSync
import com.rendox.grocerygenius.data.model.asEntity
import com.rendox.grocerygenius.database.grocery_icon.IconDao
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.IconReference
import com.rendox.grocerygenius.network.icons.IconNetworkDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IconRepositoryImpl @Inject constructor(
    private val iconDao: IconDao,
    private val iconNetworkDataSource: IconNetworkDataSource,
) : IconRepository {

    override fun getIconsGroupedByCategory(): Flow<Map<Category, List<IconReference>>> {
        return iconDao.getIconsGroupedByCategory()
    }

    override suspend fun getGroceryIconsByKeywords(keywords: List<String>): List<IconReference> {
        return iconDao.getGroceryIconsByKeywords(keywords)
    }

    override suspend fun getGroceryIconsByName(name: String): List<IconReference> {
        return iconDao.getGroceryIconsByName(name)
    }

    override suspend fun syncWith(synchronizer: Synchronizer) = synchronizer.changeListSync(
        prepopulateWithInitialData = {
            val icons = iconNetworkDataSource.downloadIcons()
            iconDao.insertGroceryIcons(icons.map { it.asEntity() })
        },
        versionReader = { it.iconVersion },
        changeListFetcher = { currentVersion ->
            iconNetworkDataSource.getIconChangeList(after = currentVersion)
        },
        versionUpdater = { latestVersion ->
            copy(iconVersion = latestVersion)
        },
        modelDeleter = { iconIds ->
            iconDao.deleteIcons(iconIds)
        },
        modelUpdater = { changedIds ->
            val networkIcon = iconNetworkDataSource.downloadIconsByIds(ids = changedIds)
            iconDao.upsertGroceryIcons(networkIcon.map { it.asEntity() })
        },
    )
}
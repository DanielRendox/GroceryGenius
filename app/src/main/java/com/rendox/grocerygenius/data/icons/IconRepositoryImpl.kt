package com.rendox.grocerygenius.data.icons

import com.rendox.grocerygenius.data.Synchronizer
import com.rendox.grocerygenius.data.changeListSync
import com.rendox.grocerygenius.data.model.asEntity
import com.rendox.grocerygenius.database.grocery_icon.IconDao
import com.rendox.grocerygenius.network.icons.IconNetworkDataSource
import javax.inject.Inject

class IconRepositoryImpl @Inject constructor(
    private val iconDao: IconDao,
    private val iconNetworkDataSource: IconNetworkDataSource,
) : IconRepository {

    override suspend fun getAllGroceryIcons() = iconDao.getAllGroceryIcons()

    override suspend fun syncWith(synchronizer: Synchronizer) = synchronizer.changeListSync(
        checkIfExistingDataIsEmpty = {
            iconDao.getAllGroceryIcons().isEmpty()
        },
        prepopulateWithInitialData = {
            val icons = iconNetworkDataSource.downloadIcons()
            iconDao.upsertGroceryIcons(icons.map { it.asEntity() })
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
package com.rendox.grocerygenius.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.rendox.grocerygenius.model.ChangeListVersions
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class ChangeListVersionsDataSource @Inject constructor(
    private val userPreferences: DataStore<Preferences>,
) {
    suspend fun getChangeListVersions() = userPreferences.data
        .map {
            ChangeListVersions(
                iconVersion = it[ICON_VERSION] ?: -1,
                categoryVersion = it[CATEGORY_VERSION] ?: -1,
                productVersion = it[PRODUCT_VERSION] ?: -1,
            )
        }
        .firstOrNull() ?: ChangeListVersions()

    suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions) {
        try {
            userPreferences.edit { currentPreferences ->
                val updatedChangeListVersions = update(
                    ChangeListVersions(
                        iconVersion = currentPreferences[ICON_VERSION] ?: -1,
                        categoryVersion = currentPreferences[CATEGORY_VERSION] ?: -1,
                        productVersion = currentPreferences[PRODUCT_VERSION] ?: -1,
                    ),
                )

                currentPreferences[ICON_VERSION] = updatedChangeListVersions.iconVersion
                currentPreferences[CATEGORY_VERSION] = updatedChangeListVersions.categoryVersion
                currentPreferences[PRODUCT_VERSION] = updatedChangeListVersions.productVersion
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    companion object {
        val ICON_VERSION = intPreferencesKey("icon_version")
        val CATEGORY_VERSION = intPreferencesKey("category_version")
        val PRODUCT_VERSION = intPreferencesKey("product_version")
    }
}
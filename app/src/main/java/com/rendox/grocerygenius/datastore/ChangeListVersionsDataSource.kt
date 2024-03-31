package com.rendox.grocerygenius.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
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
                iconVersion = it[PreferencesKeys.IconVersion] ?: -1,
                categoryVersion = it[PreferencesKeys.CategoryVersion] ?: -1,
                productVersion = it[PreferencesKeys.ProductVersion] ?: -1,
            )
        }
        .firstOrNull() ?: ChangeListVersions()

    suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions) {
        try {
            userPreferences.edit { currentPreferences ->
                val updatedChangeListVersions = update(
                    ChangeListVersions(
                        iconVersion = currentPreferences[PreferencesKeys.IconVersion] ?: -1,
                        categoryVersion = currentPreferences[PreferencesKeys.CategoryVersion] ?: -1,
                        productVersion = currentPreferences[PreferencesKeys.ProductVersion] ?: -1,
                    ),
                )

                currentPreferences[PreferencesKeys.IconVersion] = updatedChangeListVersions.iconVersion
                currentPreferences[PreferencesKeys.CategoryVersion] = updatedChangeListVersions.categoryVersion
                currentPreferences[PreferencesKeys.ProductVersion] = updatedChangeListVersions.productVersion
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }
}
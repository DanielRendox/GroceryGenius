package com.rendox.grocerygenius.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.rendox.grocerygenius.model.ChangeListVersions

private const val TEST_DATASTORE_NAME = "test_datastore"

class ChangeListVersionsDataSourceTest {

    private val testContext: Context = ApplicationProvider.getApplicationContext()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(testDispatcher + Job()),
            produceFile = { testContext.preferencesDataStoreFile(TEST_DATASTORE_NAME) }
        )

    private val changeListVersionsDataSource =
        ChangeListVersionsDataSource(testDataStore)

    @Test
    fun assertUpdatesVersionsSuccessfully() = runTest(testDispatcher) {
        changeListVersionsDataSource.updateChangeListVersion {
            copy(iconVersion = 1, categoryVersion = 2, productVersion = 3)
        }
        assertThat(changeListVersionsDataSource.getChangeListVersions()).isEqualTo(
            ChangeListVersions(iconVersion = 1, categoryVersion = 2, productVersion = 3)
        )
    }
}
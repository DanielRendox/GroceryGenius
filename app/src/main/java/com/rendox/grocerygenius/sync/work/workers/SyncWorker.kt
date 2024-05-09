/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rendox.grocerygenius.sync.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.rendox.grocerygenius.data.Synchronizer
import com.rendox.grocerygenius.data.category.CategoryRepository
import com.rendox.grocerygenius.data.icons.IconRepository
import com.rendox.grocerygenius.data.product.ProductRepository
import com.rendox.grocerygenius.datastore.ChangeListVersionsDataSource
import com.rendox.grocerygenius.model.ChangeListVersions
import com.rendox.grocerygenius.network.di.Dispatcher
import com.rendox.grocerygenius.network.di.GroceryGeniusDispatchers
import com.rendox.grocerygenius.sync.work.initializers.SyncConstraints
import com.rendox.grocerygenius.sync.work.initializers.syncForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Syncs the data layer by delegating to the appropriate repository instances with
 * sync functionality.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val iconRepository: IconRepository,
    @Dispatcher(GroceryGeniusDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val changeListVersionsDataSource: ChangeListVersionsDataSource,
) : CoroutineWorker(appContext, workerParams), Synchronizer {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        // sync all repositories one by one because the data is interdependent through foreign keys
        // if any sync fails, further sync operations will be cancelled
        val syncedSuccessfully = listOf(
            iconRepository,
            categoryRepository,
            productRepository,
        ).all { it.sync() }
        if (syncedSuccessfully) Result.success() else Result.retry()
    }

    override suspend fun getChangeListVersions(): ChangeListVersions =
        changeListVersionsDataSource.getChangeListVersions()

    override suspend fun updateChangeListVersions(
        update: ChangeListVersions.() -> ChangeListVersions
    ) = changeListVersionsDataSource.updateChangeListVersion(update)


    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<DelegatingWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(SyncConstraints)
                .setInputData(SyncWorker::class.delegatedData())
                .build()
        }
    }
}

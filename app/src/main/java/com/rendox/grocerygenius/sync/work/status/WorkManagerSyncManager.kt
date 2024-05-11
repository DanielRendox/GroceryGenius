package com.rendox.grocerygenius.sync.work.status

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.rendox.grocerygenius.data.util.SyncManager
import com.rendox.grocerygenius.sync.work.initializers.SYNC_WORK_NAME
import com.rendox.grocerygenius.sync.work.workers.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * [SyncManager] backed by [WorkInfo] from [WorkManager]
 */
internal class WorkManagerSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncManager {
    override val syncStatus: Flow<SyncStatus> =
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME)
            .map {
                when (it.lastOrNull()?.state) {
                    WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED, null -> SyncStatus.RUNNING
                    WorkInfo.State.SUCCEEDED -> SyncStatus.SUCCEEDED
                    WorkInfo.State.FAILED, WorkInfo.State.BLOCKED, WorkInfo.State.CANCELLED ->
                        SyncStatus.FAILED
                }
            }
            .conflate()

    override fun startOrRetrySync() {
        val workManager = WorkManager.getInstance(context)
        // Run sync on app startup and ensure only one sync worker runs at any time
        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            SyncWorker.startUpSyncWork(),
        )
    }
}
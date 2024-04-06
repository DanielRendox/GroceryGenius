package com.rendox.grocerygenius.data

import android.util.Log
import com.rendox.grocerygenius.datastore.ChangeListVersions
import com.rendox.grocerygenius.network.model.NetworkChangeList
import kotlin.coroutines.cancellation.CancellationException

interface Synchronizer {
    suspend fun getChangeListVersions(): ChangeListVersions
    suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions)

    /**
     * Syntactic sugar to call [Syncable.syncWith] while omitting the synchronizer argument
     */
    suspend fun Syncable.sync() = this@sync.syncWith(this@Synchronizer)
}

interface Syncable {
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

/**
 * Attempts [block], returning a successful [Result] if it succeeds, otherwise a [Result.Failure]
 * taking care not to break structured concurrency
 */
suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.e(
        "suspendRunCatching",
        "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result",
        exception,
    )
    Result.failure(exception)
}

/**
 * Utility function for syncing a repository with the network.
 * [versionReader] Reads the current version of the model that needs to be synced
 * [changeListFetcher] Fetches the change list for the model
 * [versionUpdater] Updates the [ChangeListVersions] after a successful sync
 * [modelDeleter] Deletes models by consuming the ids of the models that have been deleted.
 * [modelUpdater] Updates models by consuming the ids of the models that have changed.
 *
 * Note that the blocks defined above are never run concurrently, and the [Synchronizer]
 * implementation must guarantee this.
 */
suspend fun Synchronizer.changeListSync(
    checkIfExistingDataIsEmpty: suspend () -> Boolean,
    prepopulateWithInitialData: suspend () -> Unit,
    versionReader: (ChangeListVersions) -> Int,
    changeListFetcher: suspend (Int) -> List<NetworkChangeList>,
    versionUpdater: ChangeListVersions.(Int) -> ChangeListVersions,
    modelDeleter: suspend (List<String>) -> Unit,
    modelUpdater: suspend (List<String>) -> Unit,
): Boolean = suspendRunCatching {
    val localVersion = versionReader(getChangeListVersions())
    val networkChangeList = changeListFetcher(localVersion)
    val latestVersion = networkChangeList.lastOrNull()?.changeListVersion

    if (checkIfExistingDataIsEmpty()) {
        prepopulateWithInitialData()
        updateChangeListVersions {
            versionUpdater(latestVersion ?: 0)
        }
        return@suspendRunCatching true
    }
    if (latestVersion == null) return@suspendRunCatching true

    val (deleted, updated) = networkChangeList.partition(NetworkChangeList::isDeleted)
    modelDeleter(deleted.map { it.id })
    modelUpdater(updated.map { it.id })

    updateChangeListVersions {
        versionUpdater(latestVersion)
    }
}.isSuccess
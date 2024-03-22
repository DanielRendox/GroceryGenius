package com.rendox.grocerygenius.data

/**
 * Interface marker for a class that manages synchronization between local data and a remote
 * source for a [Syncable].
 */
interface Synchronizer {

    /**
     * Syntactic sugar to call [Syncable.syncWith] while omitting the synchronizer argument
     */
    suspend fun Syncable.sync() = this@sync.syncWith(this@Synchronizer)
}

interface Syncable {
    /**
     * Synchronizes the local database backing the repository with the network.
     */
    suspend fun syncWith(synchronizer: Synchronizer)
}
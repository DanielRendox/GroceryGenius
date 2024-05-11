package com.rendox.grocerygenius.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.util.ConnectivityManagerNetworkMonitor
import com.rendox.grocerygenius.data.util.SyncManager
import com.rendox.grocerygenius.sync.work.status.SyncStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val syncManager: SyncManager,
    networkMonitor: ConnectivityManagerNetworkMonitor,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val syncStatusFlow = syncManager.syncStatus
        .combine(networkMonitor.isOnline) { syncStatus, isOnline ->
            if (!isOnline) SyncStatus.OFFLINE else syncStatus
        }
        // if the operation takes longer than 10 seconds,
        // emit failed to not keep the user waiting
        .flatMapLatest { syncStatus ->
            if (syncStatus == SyncStatus.RUNNING) {
                flow {
                    emit(syncStatus)
                    delay(10_000)
                    emit(SyncStatus.FAILED)
                }
            } else {
                flowOf(syncStatus)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SyncStatus.RUNNING,
        )

    fun onRetrySync() {
        syncManager.startOrRetrySync()
    }
}
package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import com.rendox.grocerygenius.model.GroceryList
import com.rendox.grocerygenius.ui.helpers.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GroceryListsDashboardViewModel @Inject constructor(
    private val groceryListRepository: GroceryListRepository,
) : ViewModel() {

    val groceryListsFlow = groceryListRepository.getAllGroceryLists()
        .map { groceryLists ->
            groceryLists.sortedBy { it.sortingPriority }
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val _navigateToNewlyCreatedGroceryListEvent = MutableStateFlow<UiEvent<String>?>(null)
    val navigateToNewlyCreatedGroceryListEvent = _navigateToNewlyCreatedGroceryListEvent.asStateFlow()

    fun onIntent(intent: GroceryListsDashboardIntent) = when (intent) {
        is GroceryListsDashboardIntent.OnUpdateGroceryLists ->
            updateGroceryLists(intent.groceryLists)
        is GroceryListsDashboardIntent.OnCreateNewGroceryList ->
            createNewGroceryList()
    }

    private fun updateGroceryLists(
        dashboardItems: List<GroceryList>
    ) = viewModelScope.launch {
        val groceryLists = dashboardItems.mapIndexed { index, dashboardItem ->
            GroceryList(
                id = dashboardItem.id,
                name = dashboardItem.name,
                sortingPriority = index.toLong(),
                numOfGroceries = dashboardItem.numOfGroceries,
            )
        }
        groceryListRepository.upsertGroceryLists(groceryLists)
    }

    private fun createNewGroceryList() = viewModelScope.launch {
        val groceryListId = UUID.randomUUID().toString()
        groceryListRepository.insertGroceryList(
            GroceryList(
                id = groceryListId,
                name = "",
            )
        )
        _navigateToNewlyCreatedGroceryListEvent.update {
            object : UiEvent<String> {
                override val data = groceryListId
                override fun onConsumed() {
                    _navigateToNewlyCreatedGroceryListEvent.update { null }
                }
            }
        }
    }
}
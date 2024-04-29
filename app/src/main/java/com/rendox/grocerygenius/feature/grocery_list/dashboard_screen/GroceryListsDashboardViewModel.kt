package com.rendox.grocerygenius.feature.grocery_list.dashboard_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import com.rendox.grocerygenius.model.GroceryList
import com.rendox.grocerygenius.ui.helpers.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
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
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _navigateToGroceryListEvent = MutableStateFlow<UiEvent<String>?>(null)
    val navigateToGroceryListEvent = _navigateToGroceryListEvent.asStateFlow()

    fun onIntent(intent: GroceryListsDashboardUiIntent) = when (intent) {
        is GroceryListsDashboardUiIntent.OnAdderItemClick -> createNewGroceryList()
        is GroceryListsDashboardUiIntent.OnUpdateGroceryLists -> updateGroceryLists(intent.groceryLists)
    }

    private fun createNewGroceryList() {
        viewModelScope.launch {
            val groceryListId = UUID.randomUUID().toString()
            groceryListRepository.insertGroceryList(
                GroceryList(
                    id = groceryListId,
                    name = "",
                )
            )
            _navigateToGroceryListEvent.update {
                object : UiEvent<String> {
                    override val data = groceryListId
                    override fun onConsumed() {
                        _navigateToGroceryListEvent.update { null }
                    }
                }
            }
        }
    }

    private fun updateGroceryLists(dashboardItems: List<GroceryList>) {
        viewModelScope.launch {
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
    }
}
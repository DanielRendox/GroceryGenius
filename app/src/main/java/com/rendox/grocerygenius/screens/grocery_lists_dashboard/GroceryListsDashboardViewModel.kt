package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import com.rendox.grocerygenius.model.GroceryList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroceryListsDashboardViewModel @Inject constructor(
    private val groceryListRepository: GroceryListRepository,
) : ViewModel() {

    val dashboardItemsFlow = groceryListRepository.getAllGroceryLists()
        .map { groceryLists ->
            groceryLists
                .sortedBy { it.sortingPriority }
                .map { groceryList ->
                    GroceryListsDashboardItem(
                        id = groceryList.id,
                        name = groceryList.name,
                        itemCount = 10,
                        items = emptyList(),
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5000),
        )

    fun updateLists(
        dashboardItems: List<GroceryListsDashboardItem>
    ) = viewModelScope.launch {
        val groceryLists = dashboardItems.mapIndexed { index, dashboardItem ->
            GroceryList(
                id = dashboardItem.id,
                name = dashboardItem.name,
                sortingPriority = index,
            )
        }
        groceryListRepository.upsertGroceryLists(groceryLists)
    }
}
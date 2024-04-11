package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class GroceryListsDashboardViewModel @Inject constructor(
    private val groceryListRepository: GroceryListRepository,
) : ViewModel() {

    val groceryListsFlow = groceryListRepository.getAllGroceryLists()
        .map {
            it.map { groceryList ->
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

    private val orderedListIdsFlow = MutableStateFlow(emptyList<String>())

    init {
        viewModelScope.launch {
            orderedListIdsFlow.update {
                it.ifEmpty {
                    groceryListRepository.getAllGroceryLists().first().map { groceryList ->
                        groceryList.id
                    }
                }
            }
        }
    }

    fun onMoveItem(
        fromPosition: Int,
        toPosition: Int,
    ) {
        orderedListIdsFlow.update { orderedListIds ->
            orderedListIds.toMutableList().apply {
                Collections.swap(this, fromPosition, toPosition)
            }
        }
    }
}
package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GroceryListsDashboardViewModel @Inject constructor(
    groceryListRepository: GroceryListRepository,
): ViewModel() {
    val groceryListsFlow = groceryListRepository.getAllGroceryLists().map {
        it.map { groceryList ->
            GroceryListsDashboardItem(
                id = groceryList.id,
                name = groceryList.name,
                itemCount = 10,
                items = emptyList(),
            )
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(5000)
    )
}
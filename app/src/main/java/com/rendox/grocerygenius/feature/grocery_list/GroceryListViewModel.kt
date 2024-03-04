package com.rendox.grocerygenius.feature.grocery_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.model.Grocery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class GroceryListScreenViewModel : ViewModel() {
    private val _groceriesFlow = MutableStateFlow(sampleGroceryList.sortedBy { it.name }.sortedBy { it.purchased })
    val groceriesFlow = _groceriesFlow
        .map { groceryList ->
            groceryList.groupBy { it.purchased }
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyMap(),
            started = SharingStarted.WhileSubscribed(5_000),
        )

    fun toggleItemPurchased(item: Grocery) {
        _groceriesFlow.update { groceryList ->
            groceryList.toMutableList()
                .apply {
                    val indexOfItem = indexOf(item)
                    if (indexOfItem != -1) {
                        set(indexOfItem, item.copy(purchased = !item.purchased))
                    }
                }
                .sortedBy { it.name }
                .sortedBy { it.purchased }
        }
    }

    companion object {
        val sampleGroceryList = listOf(
            Grocery(name = "Milk", purchased = false, id = 1),
            Grocery(name = "Eggs", purchased = false, id = 2),
            Grocery(name = "Butter", purchased = false, id = 3),
            Grocery(name = "Apples", purchased = false, id = 4),
            Grocery(name = "Bananas", purchased = false, id = 5),
            Grocery(name = "Oranges", purchased = false, id = 6),
            Grocery(name = "Bread", purchased = false, id = 8),
            Grocery(name = "Pasta", purchased = false, id = 9),
            Grocery(name = "Rice", purchased = false, id = 10),
            Grocery(name = "Cheese", purchased = true, id = 11),
            Grocery(name = "Bread", purchased = true, id = 12),
            Grocery(name = "Pasta", purchased = true, id = 13),
            Grocery(name = "Rice", purchased = true, id = 14),
            Grocery(name = "Chicken", purchased = false, id = 15),
            Grocery(name = "Shrimp", purchased = true, id = 20),
            Grocery(name = "Crab", purchased = true, id = 21),
            Grocery(name = "Lobster", purchased = true, id = 22),
            Grocery(name = "Beef", purchased = true, id = 23),
            Grocery(name = "Pork", purchased = false, id = 24),
            Grocery(name = "Lamb", purchased = true, id = 25),
            Grocery(name = "Salmon", purchased = false, id = 26),
            Grocery(name = "Tuna", purchased = false, id = 27),
            Grocery(name = "Trout", purchased = false, id = 28),
            Grocery(name = "Cod", purchased = false, id = 29),
            Grocery(name = "Haddock", purchased = false, id = 30),
            Grocery(name = "Halibut", purchased = false, id = 31),
            Grocery(name = "Swordfish", purchased = false, id = 32),
            Grocery(name = "Mackerel", purchased = false, id = 33),
            Grocery(name = "Sardines", purchased = false, id = 34),
        )
    }
}
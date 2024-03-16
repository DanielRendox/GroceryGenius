package com.rendox.grocerygenius.screens.grocery_list.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.ui.components.grocery_list.GroceryGroup
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroceryListScreenViewModel : ViewModel() {
    private val _groceriesFlow = MutableStateFlow(sampleGroceryList)
    val groceriesFlow = _groceriesFlow
        .map { groceryList ->
            groceryList
                .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
                .sortedBy { it.purchased }
                .groupBy { it.purchased }
                .map {
                    val purchased = it.key
                    val titleId =
                        if (purchased) R.string.not_purchased_groceries_group_title else null
                    GroceryGroup(
                        titleId = titleId,
                        groceries = it.value,
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5_000),
        )

    private val _grocerySearchResults = MutableStateFlow<List<Grocery>>(emptyList())
    val grocerySearchResults: StateFlow<List<GroceryGroup>> = _grocerySearchResults
        .onEach { println("Debug bottom sheet _grocerySearchResults = $it") }
        .map { groceries ->
            listOf(
                GroceryGroup(
                    titleId = null,
                    groceries = groceries,
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
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
        }
    }

    fun onSearchInputChanged(searchInput: String) {
        if (searchInput.isNotEmpty()) {
            val searchResults = findGroceriesByName(searchInput)
                .sortedWith(
                    compareBy(
                        { !it.name.startsWith(searchInput, ignoreCase = true) },
                        { it.name }
                    )
                )

            val customGrocery = Grocery(
                id = searchResults.maxOfOrNull { it.id }?.plus(1) ?: 0,
                name = searchInput,
                purchased = true,
            )

            val isPerfectMatch =
                searchResults.firstOrNull()?.name.equals(searchInput, ignoreCase = true)

            _grocerySearchResults.update {
                if (isPerfectMatch) searchResults else searchResults + customGrocery
            }
        }
    }

    fun onBottomSheetCollapsed() {
        _grocerySearchResults.update { emptyList() }
    }

    fun onGrocerySearchResultClick(grocery: Grocery) {
        addGrocery(grocery)
    }

    fun onSearchInputKeyboardDone() {
        if (_grocerySearchResults.value.isNotEmpty()) {
            addGrocery(_grocerySearchResults.value.last())
        }
    }

    private fun addGrocery(grocery: Grocery) {
        _groceriesFlow.update { groceries ->
            groceries.toMutableList().apply {
                add(
                    grocery.copy(
                        id = groceries.maxOf { it.id } + 1,
                        purchased = false,
                    )
                )
            }
        }
        viewModelScope.launch {
            _grocerySearchResults.update { emptyList() }
        }
    }

    private fun findGroceriesByName(name: String): List<Grocery> {
        val escapedInput = Regex.escape(name)
        val pattern = Regex(".*$escapedInput.*", RegexOption.IGNORE_CASE)
        return sampleGroceryList.filter { pattern.matches(it.name) }
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
            Grocery(name = "Bwordfish", purchased = false, id = 32),
            Grocery(name = "Mackerel", purchased = false, id = 33),
            Grocery(name = "Sardines", purchased = false, id = 34),
            Grocery(name = "0 Pasta", description = "Gourmet Pasta Collection", purchased = false, id = 35),
            Grocery(name = "1 Dishwashing liquid", description = "Fresh Lemon Scent", purchased = false, id = 36),
            Grocery(name = "2 Echo Glow Smart Lamp with Alexa", description = "for kids room", purchased = false, id = 37),
        )
    }
}
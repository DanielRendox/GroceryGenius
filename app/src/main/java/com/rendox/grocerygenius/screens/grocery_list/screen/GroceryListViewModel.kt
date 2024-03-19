package com.rendox.grocerygenius.screens.grocery_list.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet.BottomSheetContentType
import com.rendox.grocerygenius.ui.components.grocery_list.GroceryGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GroceryListScreenViewModel @Inject constructor() : ViewModel() {
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

    private val _grocerySearchResultsFlow = MutableStateFlow<List<Grocery>>(emptyList())
    val grocerySearchResultsFlow: StateFlow<List<GroceryGroup>> = _grocerySearchResultsFlow
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

    var searchInput by mutableStateOf<String?>(null)
        private set

    private val searchInputFlow = snapshotFlow { searchInput }

    @OptIn(ExperimentalCoroutinesApi::class)
    var clearSearchInputButtonIsShownFlow: StateFlow<Boolean> =
        searchInputFlow
            .mapLatest { it?.isNotEmpty() ?: false }
            .stateIn(
                scope = viewModelScope,
                initialValue = false,
                started = SharingStarted.WhileSubscribed(5_000),
            )

    private val _bottomSheetContentTypeFlow = MutableStateFlow(BottomSheetContentType.Suggestions)
    val bottomSheetContentTypeFlow = _bottomSheetContentTypeFlow.asStateFlow()

    private val _previousGroceryFlow = MutableStateFlow<Grocery?>(null)
    val previousGroceryFlow = _previousGroceryFlow.asStateFlow()

    private val _editGroceryFlow = MutableStateFlow<Grocery?>(null)
    val editGroceryFlow = _editGroceryFlow.asStateFlow()

    val groceryCategories = sampleCategories

    var editGroceryDescription by mutableStateOf<String?>(null)
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    val clearEditGroceryDescriptionButtonIsShown =
        snapshotFlow { editGroceryDescription }
            .mapLatest { !it.isNullOrEmpty() }
            .stateIn(
                scope = viewModelScope,
                initialValue = false,
                started = SharingStarted.WhileSubscribed(5_000),
            )

    init {
        viewModelScope.launch {
            searchInputFlow.collectLatest { searchInput ->
                if (!searchInput.isNullOrEmpty()) {
                    updateSearchResults(searchInput)
                    _bottomSheetContentTypeFlow.update {
                        BottomSheetContentType.SearchResults
                    }
                } else {
                    _grocerySearchResultsFlow.update {
                        emptyList()
                    }
                }
            }
        }

        viewModelScope.launch {
            _bottomSheetContentTypeFlow.collect {
                println("Bottom sheet content type: $it")
            }
        }
    }

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

    fun updateSearchInput(searchInput: String) {
        this.searchInput = searchInput
        if (searchInput.isEmpty()) {
            _bottomSheetContentTypeFlow.update {
                BottomSheetContentType.Suggestions
            }
        }
    }

    fun onGrocerySearchResultClick(grocery: Grocery) {
        addGrocery(grocery)
        searchInput = null
        _previousGroceryFlow.update { grocery }
        _bottomSheetContentTypeFlow.update {
            BottomSheetContentType.RefineItemOptions
        }
    }

    fun onSearchInputKeyboardDone() {
        if (_grocerySearchResultsFlow.value.isNotEmpty()) {
            addGrocery(_grocerySearchResultsFlow.value.last())
        }
    }

    fun onClearSearchInput() {
        searchInput = null
        _bottomSheetContentTypeFlow.update {
            BottomSheetContentType.Suggestions
        }
    }

    fun onBottomSheetCollapsing() {
        searchInput = null
        _bottomSheetContentTypeFlow.update {
            BottomSheetContentType.Suggestions
        }
    }

    fun updateEditGroceryDescription(description: String) {
        editGroceryDescription = description
    }

    fun onClearEditGroceryDescription() {
        editGroceryDescription = null
    }

    fun onEditGrocery(grocery: Grocery) {
        _editGroceryFlow.update { grocery }
    }

    fun onEditGroceryCategoryClick(category: Category) {

    }

    fun onEditGroceryBottomSheetHidden() {
        editGroceryDescription = null
    }

    private fun addGrocery(grocery: Grocery) {
        _groceriesFlow.update {  groceryList ->
            groceryList.toMutableList().also { groceries ->
                val groceryIndex = groceries.indexOfFirst { it.productId == grocery.productId }
                if (groceryIndex == -1) {
                    groceries.add(
                        grocery.copy(
                            productId = groceries.maxOf { it.productId } + 1,
                            purchased = !grocery.purchased,
                        )
                    )
                } else {
                    groceries[groceryIndex] = groceries[groceryIndex].copy(
                        purchased = !groceries[groceryIndex].purchased
                    )
                }
            }
        }
        viewModelScope.launch {
            _grocerySearchResultsFlow.update { emptyList() }
        }
    }

    private fun findGroceriesByName(name: String): List<Grocery> {
        val escapedInput = Regex.escape(name)
        val pattern = Regex(".*$escapedInput.*", RegexOption.IGNORE_CASE)
        return sampleGroceryList
            .filter { pattern.matches(it.name) }
            .map { grocery ->
                Grocery(
                    productId = grocery.productId,
                    name = grocery.name,
                    purchased = _groceriesFlow.value.find { grocery.productId == it.productId }?.purchased ?: true,
                    description = grocery.description,
                    iconUri = grocery.iconUri,
                    chosenCategoryId = grocery.chosenCategoryId,
                )
            }
    }

    private fun updateSearchResults(searchInput: String) {
        val searchResults = findGroceriesByName(searchInput)
            .sortedWith(
                compareBy(
                    { !it.name.startsWith(searchInput, ignoreCase = true) },
                    { it.name }
                )
            )

        val customGrocery = Grocery(
            productId = searchResults.maxOfOrNull { it.productId }?.plus(1) ?: 0,
            name = searchInput,
            purchased = true,
            chosenCategoryId = 2,
            description = null,
            iconUri = "",
        )

        val isPerfectMatch =
            searchResults.firstOrNull()?.name.equals(searchInput, ignoreCase = true)

        _grocerySearchResultsFlow.update {
            if (isPerfectMatch) searchResults else searchResults + customGrocery
        }
    }

    companion object {
        private val sampleGroceryList = listOf(
            "Milk", "Eggs", "Butter", "Apples", "Bananas", "Oranges", "Pasta",
            "Cheese", "Bread", "Pasta", "Rice", "Chicken", "Shrimp", "Crab",
            "Lobster", "Beef", "Pork", "Lamb", "Salmon", "Tuna", "Sardines",
            "Mackerel", "Herring", "Trout", "Cod", "Haddock", "Halibut", "Flounder",
        ).mapIndexed { index, name ->
            Grocery(
                productId = index,
                name = name,
                purchased = Random.nextBoolean(),
                description = null,
                iconUri = "",
                chosenCategoryId = 1,
            )
        }

        private val sampleCategories = listOf(
            Category(
                id = 1,
                name = "Sample",
                iconUri = "",
            ),
            Category(
                id = 2,
                name = "Custom groceries",
                iconUri = "",
            ),
        )
    }
}
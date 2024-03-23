package com.rendox.grocerygenius.screens.grocery_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.data.category.CategoryRepository
import com.rendox.grocerygenius.data.grocery.GroceryRepository
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import com.rendox.grocerygenius.data.product.ProductRepository
import com.rendox.grocerygenius.model.CustomProduct
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroceryListScreenViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val groceryRepository: GroceryRepository,
    groceryListRepository: GroceryListRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val groceryListId = 1

    val groceryListFlow = groceryListRepository.getGroceryListById(groceryListId)
        .stateIn(
            scope = viewModelScope,
            initialValue = null,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    val groceriesFlow = groceryRepository.getGroceriesFromList(groceryListId)
        .map { groceryList ->
            groceryList
                .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
                .sortedBy { it.purchased }
                .groupBy { it.purchased }
                .map { group ->
                    val purchased = group.key
                    val titleId =
                        if (purchased) R.string.not_purchased_groceries_group_title else null
                    val sortedGroceries = if (purchased) {
                        group.value.sortedByDescending { it.purchasedLastModified }
                    } else {
                        group.value.sortedBy { it.category.sortingPriority }
                    }
                    GroceryGroup(
                        titleId = titleId,
                        groceries = sortedGroceries,
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5_000),
        )

    private val _grocerySearchResultsFlow = MutableStateFlow<List<Grocery>>(emptyList())
    val grocerySearchResultsFlow = _grocerySearchResultsFlow.asStateFlow()

    var searchInput by mutableStateOf<String?>(null)
        private set

    private val searchInputFlow = snapshotFlow { searchInput }

    @OptIn(ExperimentalCoroutinesApi::class)
    var clearSearchInputButtonIsShownFlow: StateFlow<Boolean> =
        searchInputFlow
            .mapLatest { !it.isNullOrEmpty() }
            .stateIn(
                scope = viewModelScope,
                initialValue = false,
                started = SharingStarted.WhileSubscribed(5_000),
            )

    private val _bottomSheetContentTypeFlow = MutableStateFlow(BottomSheetContentType.Suggestions)
    val bottomSheetContentTypeFlow = _bottomSheetContentTypeFlow.asStateFlow()

    val previousGroceryFlow = groceriesFlow
        .map { groceryGroups ->
            groceryGroups
                .flatMap { it.groceries }
                .maxByOrNull { it.purchasedLastModified }
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = null,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    private val editGroceryIdFlow = MutableStateFlow<Int?>(null)
    val editGroceryFlow = editGroceryIdFlow
        .map { editGroceryIdFlow ->
            val groceryFromRepository = groceryRepository.getGrocery(
                productId = editGroceryIdFlow ?: return@map null,
                listId = groceryListId,
            )
            println("map editGroceryFlow: groceryFromRepository = $groceryFromRepository")
            groceryFromRepository
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = null,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    var editGroceryDescription by mutableStateOf<String?>(null)
        private set

    private val editGroceryDescriptionFlow = snapshotFlow { editGroceryDescription }

    @OptIn(ExperimentalCoroutinesApi::class)
    val clearEditGroceryDescriptionButtonIsShown =
        editGroceryDescriptionFlow
            .onEach { println("editGroceryDescriptionFlow: $it") }
            .mapLatest { !it.isNullOrEmpty() }
            .stateIn(
                scope = viewModelScope,
                initialValue = false,
                started = SharingStarted.WhileSubscribed(5_000),
            )

    private val _customProduct = MutableStateFlow<CustomProduct?>(null)
    val customProduct = _customProduct.asStateFlow()

    init {
        viewModelScope.launch {
            searchInputFlow.collectLatest { searchInput ->
                val trimmedSearchInput = searchInput?.trim()
                if (!trimmedSearchInput.isNullOrEmpty()) {
                    updateSearchResults(trimmedSearchInput)
                    _bottomSheetContentTypeFlow.update {
                        BottomSheetContentType.SearchResults
                    }
                } else {
                    _grocerySearchResultsFlow.update { emptyList() }
                    _customProduct.update { null }
                }
            }
        }
        viewModelScope.launch {
            editGroceryDescriptionFlow.collectLatest { description ->
                groceryRepository.updateDescription(
                    productId = editGroceryIdFlow.value ?: return@collectLatest,
                    listId = groceryListId,
                    description = description,
                )
            }
        }
    }

    fun onIntent(intent: GroceryListScreenIntent) {
        when (intent) {
            is GroceryListScreenIntent.OnGroceryItemClick ->
                toggleItemPurchased(intent.item)

            is GroceryListScreenIntent.UpdateSearchInput ->
                updateSearchInput(intent.searchInput)

            is GroceryListScreenIntent.OnGrocerySearchResultClick ->
                addOrUpdateGrocery(intent.grocery)

            is GroceryListScreenIntent.OnSearchInputKeyboardDone ->
                onSearchInputKeyboardDone()

            is GroceryListScreenIntent.OnClearSearchInput ->
                resetAddGroceryBottomSheet()

            is GroceryListScreenIntent.OnAddGroceryBottomSheetCollapsing ->
                resetAddGroceryBottomSheet()

            is GroceryListScreenIntent.UpdateGroceryDescription ->
                editGroceryDescription = intent.description

            is GroceryListScreenIntent.OnClearGroceryDescription ->
                editGroceryDescription = null

            is GroceryListScreenIntent.OnEditGroceryClick ->
                onEditGroceryClick(intent.grocery)

            is GroceryListScreenIntent.OnCustomProductClick ->
                addCustomProduct(intent.customProduct)
        }
    }

    private fun updateSearchResults(searchInput: String) {
        viewModelScope.launch {
            val searchResults = productRepository.getProductsByName("%$searchInput%")
                .sortedWith(
                    compareBy(
                        { !it.name.startsWith(searchInput, ignoreCase = true) },
                        { it.name }
                    )
                )

            val isPerfectMatch =
                searchResults.firstOrNull()?.name.equals(searchInput, ignoreCase = true)

            _customProduct.update {
                if (!isPerfectMatch) {
                    CustomProduct(
                        name = searchInput,
                        categoryId = categoryRepository.getDefaultCategory()!!.id,
                    )
                } else null
            }

            val newResults = searchResults.map { product ->
                val correspondingGroceryInTheList = groceriesFlow.value
                    .flatMap { it.groceries }
                    .find { it.productId == product.id }
                Grocery(
                    productId = product.id,
                    name = product.name,
                    purchased = correspondingGroceryInTheList?.purchased ?: false,
                    description = correspondingGroceryInTheList?.description,
                    iconUri = product.iconUri,
                    category = product.category,
                    purchasedLastModified = correspondingGroceryInTheList?.purchasedLastModified
                        ?: System.currentTimeMillis(),
                )
            }
            _grocerySearchResultsFlow.update { newResults }
        }
    }

    private fun toggleItemPurchased(item: Grocery) {
        viewModelScope.launch {
            groceryRepository.updatePurchased(
                productId = item.productId,
                listId = groceryListId,
                purchased = !item.purchased,
            )
        }
    }

    private fun updateSearchInput(searchInput: String) {
        this.searchInput = searchInput
        if (searchInput.isEmpty()) {
            _bottomSheetContentTypeFlow.update {
                BottomSheetContentType.Suggestions
            }
        }
    }

    private fun addOrUpdateGrocery(grocery: Grocery) {
        viewModelScope.launch {
            if (groceriesFlow.value.any { it.groceries.contains(grocery) }) {
                toggleItemPurchased(grocery)
            } else {
                viewModelScope.launch {
                    groceryRepository.addGroceryToList(
                        productId = grocery.productId,
                        listId = groceryListId,
                        description = grocery.description,
                        purchased = grocery.purchased,
                    )
                }
            }
        }

        searchInput = null
        _bottomSheetContentTypeFlow.update {
            BottomSheetContentType.RefineItemOptions
        }
    }

    private fun onSearchInputKeyboardDone() {
        val customProduct = _customProduct.value
        if (customProduct != null) {
            addCustomProduct(customProduct)
        } else {
            _grocerySearchResultsFlow.value.firstOrNull()?.let {
                addOrUpdateGrocery(it)
            }
        }
    }

    private fun resetAddGroceryBottomSheet() {
        searchInput = null
        _bottomSheetContentTypeFlow.update {
            BottomSheetContentType.Suggestions
        }
    }

    private fun addCustomProduct(customProduct: CustomProduct) {
        viewModelScope.launch {
            groceryRepository.insertProductAndGrocery(
                name = customProduct.name,
                iconUri = customProduct.iconUri,
                categoryId = customProduct.categoryId,
                groceryListId = groceryListId,
                description = customProduct.description,
                purchased = false,
            )
        }

        searchInput = null
        _bottomSheetContentTypeFlow.update {
            BottomSheetContentType.RefineItemOptions
        }
    }

    private fun onEditGroceryClick(grocery: Grocery) {
        editGroceryIdFlow.update { grocery.productId }
        println("onEditGroceryClick: grocery = $grocery")
        editGroceryDescription = grocery.description
    }
}
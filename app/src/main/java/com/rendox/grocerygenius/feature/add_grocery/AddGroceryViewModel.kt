package com.rendox.grocerygenius.feature.add_grocery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.grocery.GroceryRepository
import com.rendox.grocerygenius.data.product.ProductRepository
import com.rendox.grocerygenius.model.CustomProduct
import com.rendox.grocerygenius.model.Grocery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddGroceryViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val groceryRepository: GroceryRepository,
) : ViewModel() {
    private val _uiStateFlow = MutableStateFlow(AddGroceryUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val groceryListIdFlow = MutableStateFlow<String?>(null)

    var searchQuery by mutableStateOf("")
        private set
    private val searchQueryFlow = snapshotFlow { searchQuery }

    init {
        viewModelScope.launch {
            searchQueryFlow.collectLatest { searchQuery ->
                _uiStateFlow.update {
                    it.copy(clearSearchQueryButtonIsShown = searchQuery.isNotEmpty())
                }
                val trimmedSearchInput = searchQuery.trim()
                if (trimmedSearchInput.isNotEmpty()) {
                    updateSearchResults(trimmedSearchInput)
                    _uiStateFlow.update {
                        it.copy(
                            bottomSheetContentType = AddGroceryBottomSheetContentType.SearchResults
                        )
                    }
                } else {
                    _uiStateFlow.update {
                        it.copy(
                            grocerySearchResults = emptyList(),
                            customProduct = null,
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            _uiStateFlow
                .combine(groceryListIdFlow) { uiState, groceryListId ->
                    uiState.previouslyAddedGrocery?.productId to groceryListId
                }
                .flatMapLatest { (previouslyAddedGroceryId, groceryListId) ->
                    if (previouslyAddedGroceryId != null && groceryListId != null) {
                        groceryRepository.getGroceryById(
                            productId = previouslyAddedGroceryId,
                            listId = groceryListId,
                        )
                    } else flowOf(null)
                }
                .collectLatest { previousGrocery ->
                    val displayingEditGroceryUi = _uiStateFlow.value.bottomSheetContentType ==
                            AddGroceryBottomSheetContentType.RefineItemOptions
                    val butTheGroceryIsNotInListAnymore = previousGrocery == null
                    if (displayingEditGroceryUi && butTheGroceryIsNotInListAnymore) {
                        _uiStateFlow.update {
                            it.copy(
                                bottomSheetContentType = AddGroceryBottomSheetContentType.Suggestions
                            )
                        }
                    }
                }
        }
    }

    fun onIntent(intent: AddGroceryUiIntent) = when (intent) {
        is AddGroceryUiIntent.OnUpdateSearchQuery ->
            updateSearchQuery(intent.query)

        is AddGroceryUiIntent.OnGrocerySearchResultClick ->
            addOrUpdateGrocery(intent.grocery)

        is AddGroceryUiIntent.OnSearchFieldKeyboardDone ->
            onSearchFieldKeyboardDone()

        is AddGroceryUiIntent.OnClearSearchQuery,
        AddGroceryUiIntent.OnAddGroceryBottomSheetCollapsing ->
            resetAddGroceryBottomSheet(groceryListIdFlow.value)

        is AddGroceryUiIntent.OnAddGroceryBottomSheetExpanded ->
            resetAddGroceryBottomSheet(intent.groceryListId)

        is AddGroceryUiIntent.OnCustomProductClick ->
            _uiStateFlow.value.customProduct?.let { addCustomProduct(it) }
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

            val groceries = getGroceries()
            val newResults = searchResults.map { product ->
                val correspondingGroceryInTheList = groceries.find { it.productId == product.id }
                Grocery(
                    productId = product.id,
                    name = product.name,
                    purchased = correspondingGroceryInTheList?.purchased ?: false,
                    description = correspondingGroceryInTheList?.description,
                    category = product.category,
                    purchasedLastModified = correspondingGroceryInTheList?.purchasedLastModified
                        ?: System.currentTimeMillis(),
                    icon = product.icon,
                )
            }

            _uiStateFlow.update {
                it.copy(
                    customProduct = if (!isPerfectMatch) CustomProduct(
                        name = searchInput
                    ) else null,
                    grocerySearchResults = newResults,
                )
            }
        }
    }

    private fun addOrUpdateGrocery(grocery: Grocery) {
        println("AddGroceryViewModel addOrUpdateGrocery groceryListIdFlow.value: ${groceryListIdFlow.value}")
        val groceryListId = groceryListIdFlow.value ?: return
        viewModelScope.launch {
            val groceries = getGroceries()
            println("AddGroceryViewModel addOrUpdateGrocery groceries: $groceries")
            val groceryIsAlreadyInList = groceries.any { it.productId == grocery.productId }
            if (groceryIsAlreadyInList) {
                groceryRepository.updatePurchased(
                    productId = grocery.productId,
                    listId = groceryListId,
                    purchased = !grocery.purchased,
                )
            } else {
                groceryRepository.addGroceryToList(
                    productId = grocery.productId,
                    listId = groceryListId,
                    description = grocery.description,
                    purchased = grocery.purchased,
                )
            }
            _uiStateFlow.update {
                it.copy(
                    previouslyAddedGrocery = grocery,
                    bottomSheetContentType = AddGroceryBottomSheetContentType.RefineItemOptions,
                )
            }
        }
        searchQuery = ""
    }

    private fun addCustomProduct(customProduct: CustomProduct) {
        val groceryListId = groceryListIdFlow.value ?: return
        viewModelScope.launch {
            val productId = UUID.randomUUID().toString()
            val purchased = false
            groceryRepository.insertProductAndGrocery(
                productId = productId,
                name = customProduct.name,
                categoryId = customProduct.category?.id,
                groceryListId = groceryListId,
                description = customProduct.description,
                purchased = purchased,
            )
            _uiStateFlow.update {
                it.copy(
                    previouslyAddedGrocery = Grocery(
                        productId = productId,
                        name = customProduct.name,
                        description = customProduct.description,
                        category = customProduct.category,
                        purchased = purchased,
                    ),
                    bottomSheetContentType = AddGroceryBottomSheetContentType.RefineItemOptions,
                )
            }
        }
        searchQuery = ""
    }

    private suspend fun getGroceries(): List<Grocery> {
        val groceryListId = groceryListIdFlow.value
        return if (groceryListId != null) {
            groceryRepository.getGroceriesFromList(groceryListId).first()
        } else {
            emptyList()
        }
    }

    private fun updateSearchQuery(query: String) {
        println("AddGroceryViewModel updateSearchQuery query: $query")
        this.searchQuery = query
        if (query.isEmpty()) {
            _uiStateFlow.update {
                it.copy(bottomSheetContentType = AddGroceryBottomSheetContentType.Suggestions)
            }
        }
    }

    private fun onSearchFieldKeyboardDone() {
        val customProduct = _uiStateFlow.value.customProduct
        if (customProduct != null) {
            addCustomProduct(customProduct)
        } else {
            _uiStateFlow.value.grocerySearchResults.firstOrNull()?.let {
                addOrUpdateGrocery(it)
            }
        }
    }

    private fun resetAddGroceryBottomSheet(groceryListId: String?) {
        println("AddGroceryViewModel resetAddGroceryBottomSheet groceryListId: $groceryListId")
        groceryListIdFlow.update { groceryListId }
        searchQuery = ""
        println("AddGroceryViewModel resetAddGroceryBottomSheet searchQuery: $searchQuery")
        _uiStateFlow.update {
            it.copy(bottomSheetContentType = AddGroceryBottomSheetContentType.Suggestions)
        }
    }
}
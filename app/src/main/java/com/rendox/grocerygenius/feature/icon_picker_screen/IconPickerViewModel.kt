package com.rendox.grocerygenius.feature.icon_picker_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.grocery.GroceryRepository
import com.rendox.grocerygenius.data.icons.IconRepository
import com.rendox.grocerygenius.data.product.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class IconPickerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    iconRepository: IconRepository,
    private val productRepository: ProductRepository,
    private val groceryRepository: GroceryRepository,
) : ViewModel() {

    private val editProductIdFlow: StateFlow<String?> = savedStateHandle.getStateFlow(
        key = PRODUCT_ID_ARG,
        initialValue = null,
    )
    private val groceryListId: String =
        checkNotNull(savedStateHandle[ICON_PICKER_GROCERY_LIST_ID_ARG])

    var searchQuery by mutableStateOf("")
        private set
    private val searchQueryFlow = snapshotFlow { searchQuery }

    private val _uiStateFlow = MutableStateFlow(IconPickerUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            searchQueryFlow.collectLatest { query ->
                val trimmedQuery = query.trim()
                _uiStateFlow.update { uiState ->
                    uiState.copy(
                        clearSearchQueryButtonIsShown = query.isNotEmpty(),
                        searchResultsShown = trimmedQuery.isNotEmpty(),
                    )
                }
                val searchResults = iconRepository.getGroceryIconsByName("%$trimmedQuery%")
                    .sortedWith(
                        compareBy(
                            { it.name?.startsWith(searchQuery, ignoreCase = true) == false },
                            { it.name ?: "" }
                        )
                    )
                _uiStateFlow.update { it.copy(searchResults = searchResults) }
            }
        }
        viewModelScope.launch {
            iconRepository.getIconsGroupedByCategory().collectLatest { icons ->
                _uiStateFlow.update { uiState ->
                    uiState.copy(
                        groupedIcons = icons.toSortedMap(
                            comparator = compareBy { category -> category.sortingPriority }
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            editProductIdFlow
                .mapNotNull { it }
                .flatMapLatest { productId ->
                    productRepository.getProductById(productId)
                }
                .collectLatest { product ->
                    _uiStateFlow.update { it.copy(product = product) }
                }
        }
    }

    fun onIntent(intent: IconPickerIntent) = viewModelScope.launch {
        when (intent) {
            is IconPickerIntent.OnPickIcon ->
                onPickIcon(intent.iconReference.uniqueFileName)

            is IconPickerIntent.OnUpdateSearchQuery ->
                searchQuery = intent.query

            is IconPickerIntent.OnClearSearchQuery -> searchQuery = ""
            is IconPickerIntent.OnRemoveIcon -> onPickIcon(null)
        }
    }

    private suspend fun onPickIcon(iconId: String?) {
        val editProductId = editProductIdFlow.value ?: return
        val grocery = groceryRepository.getGroceryById(
            productId = editProductId,
            listId = groceryListId,
        ).first() ?: return

        if (grocery.productIsDefault) {
            // Default products should not be changed so we create a new custom one
            val newProductId = UUID.randomUUID().toString()
            groceryRepository.insertProductAndGrocery(
                name = grocery.name,
                iconId = iconId,
                productId = newProductId,
                categoryId = grocery.category?.id,
                groceryListId = groceryListId,
                description = grocery.description,
                purchased = grocery.purchased,
                purchasedLastModified = grocery.purchasedLastModified,
                isDefault = false,
            )
            groceryRepository.removeGroceryFromList(
                productId = editProductId,
                listId = groceryListId,
            )
            savedStateHandle[PRODUCT_ID_ARG] = newProductId
        } else {
            productRepository.updateProductIcon(
                productId = editProductId,
                iconId = iconId,
            )
        }
    }
}


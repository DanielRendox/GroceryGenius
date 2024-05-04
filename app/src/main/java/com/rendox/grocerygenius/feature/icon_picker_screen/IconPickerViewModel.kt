package com.rendox.grocerygenius.feature.icon_picker_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.icons.IconRepository
import com.rendox.grocerygenius.data.product.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IconPickerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    iconRepository: IconRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val editProductId: String = checkNotNull(savedStateHandle[PRODUCT_ID_ARG])
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
            productRepository.getProductById(editProductId).collectLatest { product ->
                _uiStateFlow.update { it.copy(product = product) }
            }
        }
    }

    fun onIntent(intent: IconPickerIntent) = viewModelScope.launch {
        when (intent) {
            is IconPickerIntent.OnPickIcon -> {
                productRepository.updateProductIcon(
                    productId = editProductId,
                    iconId = intent.iconReference.uniqueFileName,
                )
            }

            is IconPickerIntent.OnUpdateSearchQuery -> {
                searchQuery = intent.query
            }

            is IconPickerIntent.OnClearSearchQuery -> {
                searchQuery = ""
            }

            is IconPickerIntent.OnRemoveIcon -> {
                productRepository.updateProductIcon(
                    productId = editProductId,
                    iconId = null,
                )
            }
        }
    }
}


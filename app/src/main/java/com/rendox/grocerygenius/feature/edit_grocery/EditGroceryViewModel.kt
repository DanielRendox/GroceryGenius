package com.rendox.grocerygenius.feature.edit_grocery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.category.CategoryRepository
import com.rendox.grocerygenius.data.grocery.GroceryRepository
import com.rendox.grocerygenius.data.product.ProductRepository
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.CompoundGroceryId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class EditGroceryViewModel @Inject constructor(
    categoryRepository: CategoryRepository,
    private val groceryRepository: GroceryRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val compoundGroceryIdFlow = MutableStateFlow<CompoundGroceryId?>(null)

    private val _uiStateFlow = MutableStateFlow(EditGroceryUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    var editGroceryDescription by mutableStateOf(TextFieldValue(""))
        private set
    private val editGroceryDescriptionFlow = snapshotFlow { editGroceryDescription.text }

    private var updateGroceryJob: Job? = null

    init {
        viewModelScope.launch {
            editGroceryDescriptionFlow.collectLatest { description ->
                _uiStateFlow.update {
                    it.copy(
                        clearEditGroceryDescriptionButtonIsShown = description.isNotEmpty()
                    )
                }
            }
        }
        viewModelScope.launch {
            categoryRepository.getAllCategories()
                .map { categories -> categories.sortedBy { it.sortingPriority } }
                .collectLatest { categories ->
                    _uiStateFlow.update {
                        it.copy(groceryCategories = categories)
                    }
                }
        }
    }

    fun onIntent(intent: EditGroceryUiIntent) = when (intent) {
        is EditGroceryUiIntent.OnDescriptionChanged ->
            editGroceryDescription = intent.description

        is EditGroceryUiIntent.OnClearDescription ->
            editGroceryDescription = TextFieldValue("")

        is EditGroceryUiIntent.OnCategorySelected ->
            onCategorySelected(intent.category)

        is EditGroceryUiIntent.OnCustomCategorySelected ->
            onCategorySelected(null)

        is EditGroceryUiIntent.OnRemoveGroceryFromList ->
            onRemoveGroceryFromList()

        is EditGroceryUiIntent.OnDeleteProduct ->
            onDeleteProduct()

        is EditGroceryUiIntent.OnEditOtherGrocery ->
            onEditOtherGrocery(intent.productId, intent.groceryListId)
    }

    private fun onCategorySelected(category: Category?) {
        viewModelScope.launch {
            compoundGroceryIdFlow.value?.let { compoundGroceryId ->
                val grocery = groceryRepository.getGroceryById(
                    productId = compoundGroceryId.productId,
                    listId = compoundGroceryId.groceryListId,
                ).first() ?: return@launch

                if (grocery.productIsDefault) {
                    // Default products should not be changed so we create a new custom one
                    val newProductId = UUID.randomUUID().toString()
                    groceryRepository.insertProductAndGrocery(
                        name = grocery.name,
                        iconId = grocery.icon?.uniqueFileName,
                        productId = newProductId,
                        categoryId = category?.id,
                        groceryListId = compoundGroceryId.groceryListId,
                        description = grocery.description,
                        purchased = grocery.purchased,
                        purchasedLastModified = grocery.purchasedLastModified,
                        isDefault = false,
                    )
                    groceryRepository.removeGroceryFromList(
                        productId = compoundGroceryId.productId,
                        listId = compoundGroceryId.groceryListId,
                    )
                    onEditOtherGrocery(
                        productId = newProductId,
                        groceryListId = compoundGroceryId.groceryListId,
                    )
                } else {
                    productRepository.updateProductCategory(
                        productId = compoundGroceryId.productId,
                        categoryId = category?.id,
                    )
                }
            }
            _uiStateFlow.update { uiState ->
                uiState.copy(
                    editGrocery = uiState.editGrocery?.copy(category = category)
                )
            }
        }
    }

    private fun onRemoveGroceryFromList() {
        viewModelScope.launch {
            compoundGroceryIdFlow.value?.let { (productId, groceryListId) ->
                groceryRepository.removeGroceryFromList(
                    productId = productId,
                    listId = groceryListId,
                )
            }
        }
    }

    private fun onDeleteProduct() {
        viewModelScope.launch {
            compoundGroceryIdFlow.value?.productId?.let {
                productRepository.deleteProductById(it)
            }
        }
    }

    private fun onEditOtherGrocery(productId: String, groceryListId: String) {
        compoundGroceryIdFlow.update {
            CompoundGroceryId(
                productId = productId,
                groceryListId = groceryListId,
            )
        }
        updateGroceryJob?.cancel()
        updateGroceryJob = viewModelScope.launch {
            val grocery = groceryRepository.getGroceryById(
                productId = productId,
                listId = groceryListId,
            ).first() ?: return@launch
            val nameLength = grocery.description?.length ?: 0
            editGroceryDescription = TextFieldValue(
                text = grocery.description ?: "",
                selection = TextRange(nameLength, nameLength),
            )
            _uiStateFlow.update { it.copy(editGrocery = grocery) }
            editGroceryDescriptionFlow
                .debounce(800)
                .collectLatest { description ->
                    compoundGroceryIdFlow.value?.let { (productId, groceryListId) ->
                        groceryRepository.updateDescription(
                            productId = productId,
                            listId = groceryListId,
                            description = description,
                        )
                    }
                }
        }
    }
}
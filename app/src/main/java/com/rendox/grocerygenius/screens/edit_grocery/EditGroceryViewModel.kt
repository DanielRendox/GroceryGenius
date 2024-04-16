package com.rendox.grocerygenius.screens.edit_grocery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.category.CategoryRepository
import com.rendox.grocerygenius.data.grocery.GroceryRepository
import com.rendox.grocerygenius.data.icons.IconRepository
import com.rendox.grocerygenius.data.product.ProductRepository
import com.rendox.grocerygenius.model.CompoundGroceryId
import com.rendox.grocerygenius.model.Grocery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGroceryViewModel @Inject constructor(
    categoryRepository: CategoryRepository,
    private val groceryRepository: GroceryRepository,
    private val productRepository: ProductRepository,
    private val iconRepository: IconRepository,
) : ViewModel() {
    private val compoundGroceryIdFlow = MutableStateFlow<CompoundGroceryId?>(null)

    private val _screenStateFlow = MutableStateFlow(EditGroceryScreenState())
    val screenState = _screenStateFlow.asStateFlow()

    var editGroceryDescription by mutableStateOf<String?>(null)
        private set
    private val editGroceryDescriptionFlow = snapshotFlow { editGroceryDescription }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val groceryFlow: Flow<Grocery> = compoundGroceryIdFlow
        .flatMapLatest { compoundGroceryId ->
            compoundGroceryId?.let { (productId, groceryListId) ->
                groceryRepository.getGroceryById(
                    productId = productId,
                    listId = groceryListId,
                )
            } ?: emptyFlow()
        }
        .filterNotNull()

    init {
        viewModelScope.launch {
            groceryFlow.collectLatest { grocery ->
                _screenStateFlow.update { it.copy(editGrocery = grocery) }
            }
        }
        viewModelScope.launch {
            editGroceryDescriptionFlow.collectLatest { description ->
                _screenStateFlow.update {
                    it.copy(
                        clearEditGroceryDescriptionButtonIsShown = !description.isNullOrEmpty()
                    )
                }
                compoundGroceryIdFlow.value?.let { (productId, groceryListId) ->
                    groceryRepository.updateDescription(
                        productId = productId,
                        listId = groceryListId,
                        description = description,
                    )
                }
            }
        }
        viewModelScope.launch {
            categoryRepository.getAllCategories().collectLatest { categories ->
                _screenStateFlow.update {
                    it.copy(groceryCategories = categories)
                }
            }
        }
        viewModelScope.launch {
            iconRepository.getAllGroceryIcons().collectLatest { icons ->
                println("all grocery icons: $icons")
                _screenStateFlow.update { screenState ->
                    screenState.copy(icons = icons.sortedBy { it.name })
                }
            }
        }
        viewModelScope.launch {
            editGroceryDescriptionFlow.collect {
                println("EditGroceryViewModel: editGroceryDescriptionFlow: $it")
            }
        }
    }

    fun onIntent(intent: EditGroceryScreenIntent) = when (intent) {
        is EditGroceryScreenIntent.OnDescriptionChanged ->
            editGroceryDescription = intent.description

        is EditGroceryScreenIntent.OnClearDescription ->
            editGroceryDescription = null

        is EditGroceryScreenIntent.OnCategorySelected ->
            onCategorySelected(intent.categoryId)

        is EditGroceryScreenIntent.OnCustomCategorySelected ->
            onCategorySelected(null)

        is EditGroceryScreenIntent.OnIconSelected ->
            onIconSelected(intent.iconId)

        is EditGroceryScreenIntent.OnRemoveGroceryFromList ->
            onRemoveGroceryFromList()

        is EditGroceryScreenIntent.OnDeleteProduct ->
            onDeleteProduct()

        is EditGroceryScreenIntent.OnEditOtherGrocery ->
            onEditOtherGrocery(intent.productId, intent.groceryListId)
    }

    private fun onCategorySelected(categoryId: String?) {
        viewModelScope.launch {
            compoundGroceryIdFlow.value?.productId?.let {
                productRepository.updateProductCategory(
                    productId = it,
                    categoryId = categoryId,
                )
            }
        }
    }

    private fun onIconSelected(iconId: String) {
        viewModelScope.launch {
            compoundGroceryIdFlow.value?.productId?.let {
                productRepository.updateProductIcon(
                    productId = it,
                    iconId = iconId,
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
        viewModelScope.launch {
            editGroceryDescription = groceryFlow.first().description
        }
        compoundGroceryIdFlow.update {
            CompoundGroceryId(
                productId = productId,
                groceryListId = groceryListId,
            )
        }
    }
}
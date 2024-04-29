package com.rendox.grocerygenius.feature.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.category.CategoryRepository
import com.rendox.grocerygenius.data.grocery.GroceryRepository
import com.rendox.grocerygenius.data.product.ProductRepository
import com.rendox.grocerygenius.model.Grocery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    categoryRepository: CategoryRepository,
    productRepository: ProductRepository,
    private val groceryRepository: GroceryRepository,
) : ViewModel() {
    val categoryId: String = checkNotNull(savedStateHandle[CATEGORY_ID_NAV_ARG])
    val groceryListId: String = checkNotNull(savedStateHandle[GROCERY_LIST_ID_NAV_ARG])

    private val groceriesInList = groceryRepository.getGroceriesFromList(groceryListId)

    val categoryNameFlow = categoryRepository.getCategoryById(categoryId)
        .map { it?.name ?: "" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "",
        )

    private val _groceriesFlow = MutableStateFlow(emptyList<Grocery>())
    val groceriesFlow = _groceriesFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val products = productRepository.getProductsByCategory(categoryId)
            groceriesInList.collectLatest { groceriesFromList ->
                val groceries = products.map { product ->
                    val groceryFromList = groceriesFromList.find { it.productId == product.id }
                    Grocery(
                        productId = product.id,
                        name = product.name,
                        purchased = groceryFromList?.purchased ?: true,
                        icon = product.icon,
                        category = product.category,
                        productIsDefault = product.isDefault,
                    )
                }
                _groceriesFlow.update { groceries }
            }
        }
    }

    fun onGroceryClick(grocery: Grocery) {
        viewModelScope.launch {
            val groceryIsAlreadyInList =
                groceriesInList.first().any { it.productId == grocery.productId }
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
        }
    }
}
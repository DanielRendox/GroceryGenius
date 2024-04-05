package com.rendox.grocerygenius.screens.grocery_list

import com.rendox.grocerygenius.model.CustomProduct
import com.rendox.grocerygenius.ui.GroceryPresentation

sealed class GroceryListScreenIntent {
    data class OnGroceryItemClick(val item: GroceryPresentation) : GroceryListScreenIntent()
    data class UpdateSearchInput(val searchInput: String) : GroceryListScreenIntent()
    data class OnGrocerySearchResultClick(val grocery: GroceryPresentation) : GroceryListScreenIntent()
    data object OnSearchInputKeyboardDone : GroceryListScreenIntent()
    data object OnClearSearchInput : GroceryListScreenIntent()
    data object OnAddGroceryBottomSheetCollapsing : GroceryListScreenIntent()
    data class UpdateGroceryDescription(val description: String) : GroceryListScreenIntent()
    data object OnClearGroceryDescription : GroceryListScreenIntent()
    data class OnEditGroceryClick(val grocery: GroceryPresentation) : GroceryListScreenIntent()
    data class OnCustomProductClick(val customProduct: CustomProduct) : GroceryListScreenIntent()
    data class OnCategorySelected(val categoryId: String): GroceryListScreenIntent()
    data object OnCustomCategorySelected : GroceryListScreenIntent()
    data class OnIconSelected(val iconId: String): GroceryListScreenIntent()
    data class OnRemoveGroceryFromList(val groceryId: String): GroceryListScreenIntent()
    data class OnDeleteProduct(val productId: String): GroceryListScreenIntent()
}
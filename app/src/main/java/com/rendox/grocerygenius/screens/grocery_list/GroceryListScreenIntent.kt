package com.rendox.grocerygenius.screens.grocery_list

import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Grocery

sealed class GroceryListScreenIntent {
    data class OnGroceryItemClick(val item: Grocery) : GroceryListScreenIntent()
    data class UpdateSearchInput(val searchInput: String) : GroceryListScreenIntent()
    data class OnGrocerySearchResultClick(val grocery: Grocery) : GroceryListScreenIntent()
    data object OnSearchInputKeyboardDone : GroceryListScreenIntent()
    data object OnClearSearchInput : GroceryListScreenIntent()
    data object OnBottomSheetCollapsing : GroceryListScreenIntent()
    data class UpdateGroceryDescription(val description: String) : GroceryListScreenIntent()
    data object OnClearGroceryDescription : GroceryListScreenIntent()
    data class OnEditGroceryClick(val grocery: Grocery) : GroceryListScreenIntent()
    data class OnEditGroceryCategoryClick(val category: Category) : GroceryListScreenIntent()
    data object OnEditGroceryBottomSheetHidden : GroceryListScreenIntent()
}
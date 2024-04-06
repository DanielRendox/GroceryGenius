package com.rendox.grocerygenius.screens.grocery_list

import com.rendox.grocerygenius.model.Grocery

sealed interface GroceryListScreenIntent {
    data class OnGroceryItemClick(val item: Grocery) : GroceryListScreenIntent
    data class OnSearchQueryChanged(val searchInput: String) : GroceryListScreenIntent
    data class OnGrocerySearchResultClick(val grocery: Grocery) : GroceryListScreenIntent
    data object OnSearchFieldKeyboardDone : GroceryListScreenIntent
    data object OnClearSearchQuery : GroceryListScreenIntent
    data object OnAddGroceryBottomSheetCollapsing : GroceryListScreenIntent
    data object OnCustomProductClick : GroceryListScreenIntent
}
package com.rendox.grocerygenius.feature.add_grocery

import com.rendox.grocerygenius.model.Grocery

sealed interface AddGroceryUiIntent {
    data class OnUpdateSearchQuery(val query: String) : AddGroceryUiIntent
    data class OnGrocerySearchResultClick(val grocery: Grocery) : AddGroceryUiIntent
    data object OnSearchFieldKeyboardDone : AddGroceryUiIntent
    data object OnClearSearchQuery : AddGroceryUiIntent
    data object OnAddGroceryBottomSheetCollapsing : AddGroceryUiIntent
    data class OnAddGroceryBottomSheetExpanded(val groceryListId: String) : AddGroceryUiIntent
    data object OnCustomProductClick : AddGroceryUiIntent
}
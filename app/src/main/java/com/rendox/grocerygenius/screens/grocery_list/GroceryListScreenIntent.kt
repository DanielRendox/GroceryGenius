package com.rendox.grocerygenius.screens.grocery_list

import androidx.compose.ui.text.input.TextFieldValue
import com.rendox.grocerygenius.model.Grocery

sealed interface GroceryListScreenIntent {
    data class OnGroceryItemClick(val item: Grocery) : GroceryListScreenIntent
    data class OnSearchQueryChanged(val searchInput: String) : GroceryListScreenIntent
    data class OnGrocerySearchResultClick(val grocery: Grocery) : GroceryListScreenIntent
    data object OnSearchFieldKeyboardDone : GroceryListScreenIntent
    data object OnClearSearchQuery : GroceryListScreenIntent
    data object OnAddGroceryBottomSheetCollapsing : GroceryListScreenIntent
    data object OnCustomProductClick : GroceryListScreenIntent
    data class UpdateGroceryListName(val name: TextFieldValue) : GroceryListScreenIntent
    data object OnKeyboardHidden : GroceryListScreenIntent
    data object OnDeleteGroceryList : GroceryListScreenIntent
    data class OnEditGroceryListToggle(val editModeIsEnabled: Boolean) : GroceryListScreenIntent
}
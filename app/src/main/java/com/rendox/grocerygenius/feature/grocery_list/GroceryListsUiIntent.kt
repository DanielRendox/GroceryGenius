package com.rendox.grocerygenius.feature.grocery_list

import androidx.compose.ui.text.input.TextFieldValue
import com.rendox.grocerygenius.model.Grocery

sealed interface GroceryListsUiIntent {
    data class OnGroceryItemClick(val item: Grocery) : GroceryListsUiIntent
    data class UpdateGroceryListName(val name: TextFieldValue) : GroceryListsUiIntent
    data object OnKeyboardHidden : GroceryListsUiIntent
    data object OnDeleteGroceryList : GroceryListsUiIntent
    data class OnEditGroceryListToggle(val editModeIsEnabled: Boolean) : GroceryListsUiIntent
}
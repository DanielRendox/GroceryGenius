package com.rendox.grocerygenius.feature.grocery_list

import androidx.compose.ui.text.input.TextFieldValue
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.model.GroceryList

sealed interface GroceryListsUiIntent {
    data class OnGroceryItemClick(val item: Grocery) : GroceryListsUiIntent
    data class UpdateGroceryListName(val name: TextFieldValue) : GroceryListsUiIntent
    data object OnKeyboardHidden : GroceryListsUiIntent
    data object OnDeleteGroceryList : GroceryListsUiIntent
    data class OnEditGroceryListToggle(val editModeIsEnabled: Boolean) : GroceryListsUiIntent
    data class OnUpdateGroceryLists(val groceryLists: List<GroceryList>) : GroceryListsUiIntent
    data object OnCreateNewGroceryList : GroceryListsUiIntent
    data class OnOpenGroceryList(val id: String) : GroceryListsUiIntent
}
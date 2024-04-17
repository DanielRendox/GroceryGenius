package com.rendox.grocerygenius.screens.edit_grocery

import androidx.compose.ui.text.input.TextFieldValue

sealed interface EditGroceryScreenIntent {
    data class OnDescriptionChanged(val description: TextFieldValue) : EditGroceryScreenIntent
    data object OnClearDescription : EditGroceryScreenIntent
    data class OnCategorySelected(val categoryId: String) : EditGroceryScreenIntent
    data object OnCustomCategorySelected : EditGroceryScreenIntent
    data class OnIconSelected(val iconId: String) : EditGroceryScreenIntent
    data object OnRemoveGroceryFromList : EditGroceryScreenIntent
    data object OnDeleteProduct : EditGroceryScreenIntent

    data class OnEditOtherGrocery(
        val productId: String,
        val groceryListId: String,
    ) : EditGroceryScreenIntent
}
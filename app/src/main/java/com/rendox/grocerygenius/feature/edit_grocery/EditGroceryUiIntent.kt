package com.rendox.grocerygenius.feature.edit_grocery

import androidx.compose.ui.text.input.TextFieldValue
import com.rendox.grocerygenius.model.Category

sealed interface EditGroceryUiIntent {
    data class OnDescriptionChanged(val description: TextFieldValue) : EditGroceryUiIntent
    data object OnClearDescription : EditGroceryUiIntent
    data class OnCategorySelected(val category: Category) : EditGroceryUiIntent
    data object OnCustomCategorySelected : EditGroceryUiIntent
    data class OnIconSelected(val iconId: String) : EditGroceryUiIntent
    data object OnRemoveGroceryFromList : EditGroceryUiIntent
    data object OnDeleteProduct : EditGroceryUiIntent

    data class OnEditOtherGrocery(
        val productId: String,
        val groceryListId: String,
    ) : EditGroceryUiIntent
}
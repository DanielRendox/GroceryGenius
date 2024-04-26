package com.rendox.grocerygenius.feature.edit_grocery

import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.model.IconReference

data class EditGroceryUiState(
    val editGrocery: Grocery? = null,
    val clearEditGroceryDescriptionButtonIsShown: Boolean = false,
    val groceryCategories: List<Category> = emptyList(),
    val icons: List<IconReference> = emptyList(),
)

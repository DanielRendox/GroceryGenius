package com.rendox.grocerygenius.screens.edit_grocery

import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.model.IconReference

data class EditGroceryScreenState(
    val editGrocery: Grocery? = null,
    val clearEditGroceryDescriptionButtonIsShown: Boolean = false,
    val groceryCategories: List<Category> = emptyList(),
    val icons: List<IconReference> = emptyList(),
)

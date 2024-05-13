package com.rendox.grocerygenius.feature.add_grocery

import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.model.Product

data class AddGroceryUiState(
    val previouslyAddedGrocery: Grocery? = null,
    val bottomSheetContentType: AddGroceryBottomSheetContentType = AddGroceryBottomSheetContentType.Suggestions,
    val grocerySearchResults: List<Grocery> = emptyList(),
    val customProducts: List<Product> = emptyList(),
    val clearSearchQueryButtonIsShown: Boolean = false,
)
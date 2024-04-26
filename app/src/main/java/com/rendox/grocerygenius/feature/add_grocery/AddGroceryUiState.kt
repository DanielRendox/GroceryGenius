package com.rendox.grocerygenius.feature.add_grocery

import com.rendox.grocerygenius.model.CustomProduct
import com.rendox.grocerygenius.model.Grocery

data class AddGroceryUiState(
    val previouslyAddedGrocery: Grocery? = null,
    val bottomSheetContentType: AddGroceryBottomSheetContentType = AddGroceryBottomSheetContentType.Suggestions,
    val grocerySearchResults: List<Grocery> = emptyList(),
    val customProduct: CustomProduct? = null,
    val clearSearchQueryButtonIsShown: Boolean = false,
)
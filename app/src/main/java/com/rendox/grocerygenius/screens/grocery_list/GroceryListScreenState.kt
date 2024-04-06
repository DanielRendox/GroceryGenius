package com.rendox.grocerygenius.screens.grocery_list

import com.rendox.grocerygenius.model.CustomProduct
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet.BottomSheetContentType

data class GroceryListScreenState(
    val listName: String = "",
    val bottomSheetContentType: BottomSheetContentType = BottomSheetContentType.Suggestions,
    val grocerySearchResults: List<Grocery> = emptyList(),
    val customProduct: CustomProduct? = null,
    val previousGrocery: Grocery? = null,
    val clearSearchQueryButtonIsShown: Boolean = false,
)

package com.rendox.grocerygenius.ui.components.grocery_list

import androidx.annotation.StringRes
import com.rendox.grocerygenius.ui.GroceryPresentation

data class GroceryGroup(
    @StringRes val titleId: Int?,
    val groceries: List<GroceryPresentation>,
)

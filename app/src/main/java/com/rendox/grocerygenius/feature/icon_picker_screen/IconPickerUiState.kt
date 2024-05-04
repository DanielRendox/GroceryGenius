package com.rendox.grocerygenius.feature.icon_picker_screen

import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.IconReference
import com.rendox.grocerygenius.model.Product

data class IconPickerUiState(
    val groupedIcons: Map<Category, List<IconReference>> = emptyMap(),
    val searchResults: List<IconReference> = emptyList(),
    val product: Product? = null,
    val clearSearchQueryButtonIsShown: Boolean = false,
    val searchResultsShown: Boolean = false,
)

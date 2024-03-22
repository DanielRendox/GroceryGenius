package com.rendox.grocerygenius.ui.components.grocery_list

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
class DefaultGroceryListItemColors(
    val defaultBackgroundColor: Color,
    val purchasedBackgroundColor: Color,
)

val ColorScheme.groceryListItemColors: DefaultGroceryListItemColors
    get() = DefaultGroceryListItemColors(
        defaultBackgroundColor = primaryContainer,
        purchasedBackgroundColor = tertiaryContainer,
    )
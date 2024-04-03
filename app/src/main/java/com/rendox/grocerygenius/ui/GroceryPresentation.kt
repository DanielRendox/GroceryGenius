package com.rendox.grocerygenius.ui

import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Grocery

data class GroceryPresentation(
    val productId: String,
    val name: String,
    val purchased: Boolean,
    val description: String? = null,
    val icon: IconPresentation? = null,
    val category: Category? = null,
    val purchasedLastModified: Long = System.currentTimeMillis(),
    val productIsDefault: Boolean = false,
)

fun Grocery.asPresentationModel(icon: IconPresentation?) = GroceryPresentation(
    productId = productId,
    name = name,
    purchased = purchased,
    description = description,
    icon = icon,
    category = category,
    purchasedLastModified = purchasedLastModified,
    productIsDefault = productIsDefault,
)

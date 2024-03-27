package com.rendox.grocerygenius.ui

import android.graphics.Bitmap
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.model.Icon

data class GroceryPresentation(
    val productId: Int,
    val name: String,
    val purchased: Boolean,
    val description: String? = null,
    val icon: Icon? = null,
    val iconBitmap: Bitmap? = null,
    val category: Category? = null,
    val purchasedLastModified: Long = System.currentTimeMillis(),
)

fun Grocery.asPresentationModel(iconBitmap: Bitmap?) = GroceryPresentation(
    productId = productId,
    name = name,
    purchased = purchased,
    description = description,
    icon = icon,
    iconBitmap = iconBitmap,
    category = category,
    purchasedLastModified = purchasedLastModified,
)

package com.rendox.grocerygenius.model

import android.graphics.Bitmap

data class Grocery(
    val productId: Int,
    val name: String,
    val purchased: Boolean,
    val description: String? = null,
    val iconId: Int? = null,
    val icon: Bitmap? = null,
    val category: Category? = null,
    val purchasedLastModified: Long = System.currentTimeMillis(),
)
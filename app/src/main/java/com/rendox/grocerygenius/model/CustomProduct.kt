package com.rendox.grocerygenius.model

import android.graphics.Bitmap

data class CustomProduct(
    val name: String,
    val description: String? = null,
    val iconUri: String? = null,
    val icon: Bitmap? = null,
    val category: Category? = null,
)
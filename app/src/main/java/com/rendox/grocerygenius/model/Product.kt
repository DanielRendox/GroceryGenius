package com.rendox.grocerygenius.model

import android.graphics.Bitmap

data class Product(
    val id: Int = 0,
    val name: String,
    val iconId: Int? = null,
    val icon: Bitmap? = null,
    val category: Category? = null,
    val deletable: Boolean = true,
)
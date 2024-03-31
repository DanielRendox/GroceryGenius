package com.rendox.grocerygenius.model

data class Grocery(
    val productId: String,
    val name: String,
    val purchased: Boolean,
    val description: String? = null,
    val icon: Icon? = null,
    val category: Category? = null,
    val purchasedLastModified: Long = System.currentTimeMillis(),
)
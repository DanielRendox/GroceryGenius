package com.rendox.grocerygenius.model

data class Grocery(
    val productId: Int,
    val name: String,
    val purchased: Boolean,
    val description: String? = null,
    val iconUri: String,
    val chosenCategoryId: Int,
)
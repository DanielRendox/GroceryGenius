package com.rendox.grocerygenius.model

data class CustomProduct(
    val name: String,
    val description: String? = null,
    val iconUri: String? = null,
    val categoryId: Int,
)
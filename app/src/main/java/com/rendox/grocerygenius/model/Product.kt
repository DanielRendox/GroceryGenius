package com.rendox.grocerygenius.model

data class Product(
    val id: Int = 0,
    val name: String,
    val iconUri: String? = null,
    val category: Category,
    val deletable: Boolean = true,
)
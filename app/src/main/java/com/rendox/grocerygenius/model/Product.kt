package com.rendox.grocerygenius.model

data class Product(
    val id: Int,
    val name: String,
    val iconUri: String,
    val categoryId: Int,
    val deletable: Boolean,
)
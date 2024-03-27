package com.rendox.grocerygenius.model

data class Product(
    val id: Int = 0,
    val name: String,
    val icon: Icon? = null,
    val category: Category? = null,
    val deletable: Boolean = true,
)
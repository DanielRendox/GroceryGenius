package com.rendox.grocerygenius.model

import java.util.UUID

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val icon: Icon? = null,
    val category: Category? = null,
    val isDefault: Boolean = false,
)
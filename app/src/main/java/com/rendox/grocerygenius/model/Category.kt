package com.rendox.grocerygenius.model

data class Category(
    val id: Int,
    val name: String,
    val sortingPriority: Int = 1,
)

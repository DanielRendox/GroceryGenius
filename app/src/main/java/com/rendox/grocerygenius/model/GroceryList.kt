package com.rendox.grocerygenius.model

data class GroceryList(
    val id: String,
    val name: String,
    val sortingPriority: Int = Int.MAX_VALUE,
)

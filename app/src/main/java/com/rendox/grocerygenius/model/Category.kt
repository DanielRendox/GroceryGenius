package com.rendox.grocerygenius.model

data class Category(
    val id: Int,
    val name: String,
    val iconUri: String,
    val sortingPriority: Int = 1,
)

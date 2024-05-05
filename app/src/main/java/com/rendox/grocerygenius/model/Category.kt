package com.rendox.grocerygenius.model

data class Category(
    val id: String,
    val name: String,
    val sortingPriority: Long = System.currentTimeMillis(),
    val defaultSortingPriority: Long = sortingPriority,
)

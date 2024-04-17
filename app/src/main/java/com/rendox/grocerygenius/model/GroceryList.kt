package com.rendox.grocerygenius.model

import java.util.UUID

data class GroceryList(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val sortingPriority: Long = System.currentTimeMillis(),
    val numOfGroceries: Int = 0,
)

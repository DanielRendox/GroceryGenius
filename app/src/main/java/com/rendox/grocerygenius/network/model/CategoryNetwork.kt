package com.rendox.grocerygenius.network.model

data class CategoryNetwork(
    val id: Int,
    val name: String,
    val iconId: Int?,
    val sortingPriority: Int = 1,
    val isDefault: Boolean = false,
)
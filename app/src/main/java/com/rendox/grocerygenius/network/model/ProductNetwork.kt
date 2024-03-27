package com.rendox.grocerygenius.network.model

data class ProductNetwork(
    val id: Int = 0,
    val name: String,
    val iconId: Int?,
    val categoryId: Int?,
    val deletable: Boolean = true,
)

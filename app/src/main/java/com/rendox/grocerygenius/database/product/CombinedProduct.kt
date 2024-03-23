package com.rendox.grocerygenius.database.product

data class CombinedProduct(
    val id: Int = 0,
    val name: String,
    val iconUri: String? = null,
    val categoryId: Int,
    val categoryName: String,
    val categoryIconUri: String,
    val categorySortingPriority: Int,
    val categoryIsDefault: Boolean,
    val deletable: Boolean = true,
)
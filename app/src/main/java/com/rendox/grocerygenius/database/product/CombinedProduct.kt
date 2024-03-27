package com.rendox.grocerygenius.database.product

data class CombinedProduct(
    val id: Int = 0,
    val name: String,
    val iconId: Int?,
    val iconFilePath: String? = null,
    val categoryId: Int?,
    val categoryName: String,
    val categorySortingPriority: Int,
    val categoryIsDefault: Boolean,
    val deletable: Boolean = true,
)
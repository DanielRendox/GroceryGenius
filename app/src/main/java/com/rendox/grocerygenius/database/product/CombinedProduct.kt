package com.rendox.grocerygenius.database.product

data class CombinedProduct(
    val id: Int,
    val name: String,
    val iconId: Int?,
    val iconFilePath: String?,
    val categoryId: Int?,
    val categoryName: String?,
    val categorySortingPriority: Int?,
    val categoryIsDefault: Boolean?,
    val deletable: Boolean,
)
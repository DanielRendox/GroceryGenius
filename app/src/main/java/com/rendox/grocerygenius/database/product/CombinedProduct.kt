package com.rendox.grocerygenius.database.product

data class CombinedProduct(
    val id: String,
    val name: String,
    val iconId: String?,
    val iconFilePath: String?,
    val categoryId: String?,
    val categoryName: String?,
    val categorySortingPriority: Int?,
    val isDefault: Boolean,
)
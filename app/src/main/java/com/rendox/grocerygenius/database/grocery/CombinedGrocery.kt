package com.rendox.grocerygenius.database.grocery

data class CombinedGrocery(
    val productId: Int,
    val name: String,
    val purchased: Boolean,
    val description: String?,
    val iconId: Int?,
    val iconFilePath: String?,
    val categoryId: Int?,
    val categoryName: String?,
    val categorySortingPriority: Int?,
    val categoryIsDefault: Boolean?,
    val purchasedLastModified: Long,
)

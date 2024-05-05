package com.rendox.grocerygenius.database.grocery

data class CombinedGrocery(
    val productId: String,
    val name: String,
    val purchased: Boolean,
    val description: String?,
    val iconId: String?,
    val iconFilePath: String?,
    val categoryId: String?,
    val categoryName: String?,
    val categorySortingPriority: Long?,
    val purchasedLastModified: Long,
    val productIsDefault: Boolean,
)

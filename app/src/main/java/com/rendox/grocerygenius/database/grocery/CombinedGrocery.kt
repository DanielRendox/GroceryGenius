package com.rendox.grocerygenius.database.grocery

data class CombinedGrocery(
    val productId: Int,
    val name: String,
    val purchased: Boolean,
    val description: String? = null,
    val iconId: Int?,
    val iconFilePath: String? = null,
    val categoryId: Int?,
    val categoryName: String?,
    val categorySortingPriority: Int = 1,
    val categoryIsDefault: Boolean = false,
    val purchasedLastModified: Long,
)

package com.rendox.grocerygenius.database.grocery

data class CombinedGrocery(
    val productId: Int,
    val name: String,
    val purchased: Boolean,
    val description: String? = null,
    val iconUri: String? = null,
    val categoryId: Int,
    val categoryName: String,
    val categoryIconUri: String,
    val categorySortingPriority: Int = 1,
    val categoryIsDefault: Boolean = false,
    val purchasedLastModified: Long,
)

package com.rendox.grocerygenius.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroceryIconAsset(
    val id: Int,
    val assetFilePath: String,
)

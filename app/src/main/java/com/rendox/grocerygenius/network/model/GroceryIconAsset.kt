package com.rendox.grocerygenius.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroceryIconAsset(
    val fileName: String,
    val assetFilePath: String,
)

package com.rendox.grocerygenius.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroceryIconAsset(
    val id: String,
    val assetFilePath: String,
)

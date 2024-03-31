package com.rendox.grocerygenius.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductNetwork(
    val id: String,
    val name: String,
    val iconId: String?,
    val categoryId: String?,
    val isDefault: Boolean,
)

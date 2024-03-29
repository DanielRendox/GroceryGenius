package com.rendox.grocerygenius.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductNetwork(
    val id: Int = 0,
    val name: String,
    val iconId: Int?,
    val categoryId: Int?,
    val editable: Boolean,
)

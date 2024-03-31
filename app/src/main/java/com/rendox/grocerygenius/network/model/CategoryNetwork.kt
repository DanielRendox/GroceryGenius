package com.rendox.grocerygenius.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryNetwork(
    val id: String,
    val name: String,
    val sortingPriority: Int = 1,
)
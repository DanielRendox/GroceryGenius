package com.rendox.grocerygenius.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkChangeList(
    val id: String,
    val changeListVersion: Int,
    val isDeleted: Boolean,
)

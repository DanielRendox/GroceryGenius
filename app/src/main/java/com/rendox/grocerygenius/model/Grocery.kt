package com.rendox.grocerygenius.model

data class Grocery(
    val id: Int,
    val name: String,
    val purchased: Boolean,
    val description: String? = null,
)
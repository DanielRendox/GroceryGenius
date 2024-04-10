package com.rendox.grocerygenius.screens.grocery_lists_dashboard

data class GroceryListsDashboardItem(
    val id: String,
    val name: String,
    val itemCount: Int,
    val items: List<GroceryListsDashboardItem>,
)

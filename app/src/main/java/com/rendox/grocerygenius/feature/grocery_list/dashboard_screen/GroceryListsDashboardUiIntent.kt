package com.rendox.grocerygenius.feature.grocery_list.dashboard_screen

import com.rendox.grocerygenius.model.GroceryList

sealed interface GroceryListsDashboardUiIntent  {
    data class OnUpdateGroceryLists(val groceryLists: List<GroceryList>) : GroceryListsDashboardUiIntent
    data object OnAdderItemClick : GroceryListsDashboardUiIntent
}
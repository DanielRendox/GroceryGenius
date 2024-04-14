package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import com.rendox.grocerygenius.model.GroceryList

sealed interface GroceryListsDashboardIntent {
    data class OnUpdateGroceryLists(
        val groceryLists: List<GroceryList>
    ) : GroceryListsDashboardIntent

    data object OnCreateNewGroceryList : GroceryListsDashboardIntent
}
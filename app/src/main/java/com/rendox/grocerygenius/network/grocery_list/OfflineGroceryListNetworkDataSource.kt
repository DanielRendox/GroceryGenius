package com.rendox.grocerygenius.network.grocery_list

import com.rendox.grocerygenius.model.GroceryList
import javax.inject.Inject

class OfflineGroceryListNetworkDataSource @Inject constructor() : GroceryListNetworkDataSource {
    override suspend fun getAllGroceryLists(): List<GroceryList> = List(4) {
        GroceryList(
            id = "sample-grocery-list-$it",
            name = "Sample Grocery List $it",
            numOfGroceries = 10,
        )
    }
}
package com.rendox.grocerygenius.network.grocery_list

import com.rendox.grocerygenius.model.GroceryList
import javax.inject.Inject

class OfflineGroceryListNetworkDataSource @Inject constructor() : GroceryListNetworkDataSource {
    override suspend fun getSampleGroceryList(): GroceryList = GroceryList(id = 1, name = "Sample Grocery List")
}
package com.rendox.grocerygenius.network.grocery_list

import com.rendox.grocerygenius.model.GroceryList

interface GroceryListNetworkDataSource {
    suspend fun getSampleGroceryList(): GroceryList
}
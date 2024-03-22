package com.rendox.grocerygenius.network.grocery_list

import com.rendox.grocerygenius.model.GroceryList
import com.rendox.grocerygenius.network.sampleGroceryList
import javax.inject.Inject

class GroceryListNetworkDataSourceFake @Inject constructor() : GroceryListNetworkDataSource {
    override suspend fun getSampleGroceryList(): GroceryList = sampleGroceryList
}
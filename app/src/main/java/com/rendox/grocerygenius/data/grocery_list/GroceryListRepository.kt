package com.rendox.grocerygenius.data.grocery_list

import com.rendox.grocerygenius.data.Syncable
import com.rendox.grocerygenius.model.GroceryList
import kotlinx.coroutines.flow.Flow

interface GroceryListRepository : Syncable {
    suspend fun insertGroceryList(groceryList: GroceryList)
    fun getGroceryListById(id: String): Flow<GroceryList?>
    fun getAllGroceryLists(): Flow<List<GroceryList>>
    suspend fun updateGroceryList(groceryList: GroceryList)
    suspend fun deleteGroceryListById(groceryListId: String)
}
package com.rendox.grocerygenius.database.grocery_icon

import androidx.room.Insert

interface GroceryIconDao {
    @Insert
    suspend fun insertGroceryIcons(groceryIconEntities: List<GroceryIconEntity>)
}
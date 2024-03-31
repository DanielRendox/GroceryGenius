package com.rendox.grocerygenius.database.grocery_list

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroceryListEntity(
    @PrimaryKey val id: String,
    val name: String,
)

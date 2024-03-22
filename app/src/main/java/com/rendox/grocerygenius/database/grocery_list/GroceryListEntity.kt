package com.rendox.grocerygenius.database.grocery_list

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroceryListEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
)

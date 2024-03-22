package com.rendox.grocerygenius.database.category

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val iconUri: String,
    val sortingPriority: Int,
    val isDefault: Boolean,
)

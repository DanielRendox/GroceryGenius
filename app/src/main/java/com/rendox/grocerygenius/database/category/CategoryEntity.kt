package com.rendox.grocerygenius.database.category

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val defaultSortingPriority: Long,
    val sortingPriority: Long,
)

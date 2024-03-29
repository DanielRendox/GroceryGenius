package com.rendox.grocerygenius.database.grocery_icon

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["filePath"], unique = true)])
data class IconEntity(
    @PrimaryKey val id: Int,
    val filePath: String,
)
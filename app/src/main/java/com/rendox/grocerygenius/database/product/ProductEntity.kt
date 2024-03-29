package com.rendox.grocerygenius.database.product

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rendox.grocerygenius.database.category.CategoryEntity
import com.rendox.grocerygenius.database.grocery_icon.IconEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = IconEntity::class,
            parentColumns = ["id"],
            childColumns = ["iconId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["iconId"]),
        Index(value = ["categoryId"]),
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val deletable: Boolean = true,
    val iconId: Int?,
    val categoryId: Int?,
)
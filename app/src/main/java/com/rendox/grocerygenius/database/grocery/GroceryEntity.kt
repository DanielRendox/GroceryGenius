package com.rendox.grocerygenius.database.grocery

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.rendox.grocerygenius.database.grocery_list.GroceryListEntity
import com.rendox.grocerygenius.database.product.ProductEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = GroceryListEntity::class,
            parentColumns = ["id"],
            childColumns = ["groceryListId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
    primaryKeys = ["productId", "groceryListId"],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["groceryListId"]),
    ]
)
data class GroceryEntity(
    val productId: Int,
    val groceryListId: Int,
    val description: String?,
    val purchased: Boolean,
    val purchasedLastModified: Long,
)

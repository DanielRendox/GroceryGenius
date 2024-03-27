package com.rendox.grocerygenius.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rendox.grocerygenius.database.category.CategoryDao
import com.rendox.grocerygenius.database.category.CategoryEntity
import com.rendox.grocerygenius.database.grocery.GroceryDao
import com.rendox.grocerygenius.database.grocery.GroceryEntity
import com.rendox.grocerygenius.database.grocery_icon.GroceryIconEntity
import com.rendox.grocerygenius.database.grocery_list.GroceryListDao
import com.rendox.grocerygenius.database.grocery_list.GroceryListEntity
import com.rendox.grocerygenius.database.product.ProductDao
import com.rendox.grocerygenius.database.product.ProductEntity

@Database(
    entities = [
        CategoryEntity::class,
        GroceryEntity::class,
        GroceryListEntity::class,
        ProductEntity::class,
        GroceryIconEntity::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun groceryDao(): GroceryDao
    abstract fun groceryListDao(): GroceryListDao
    abstract fun productDao(): ProductDao
}
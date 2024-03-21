package com.rendox.grocerygenius.network

import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.GroceryList
import com.rendox.grocerygenius.model.Product

val sampleCategoryList = listOf(
    Category(
        id = 1,
        name = "Fruits & Vegetables",
        iconUri = "",
        sortingPriority = 1,
    ),
    Category(
        id = 2,
        name = "Bakery",
        iconUri = "",
        sortingPriority = 2,
    ),
    Category(
        id = 3,
        name = "Dairy & Eggs",
        iconUri = "",
        sortingPriority = 3,
    ),
    Category(
        id = 4,
        name = "Meat",
        iconUri = "",
        sortingPriority = 4,
    ),
    Category(
        id = 5,
        name = "Seafood",
        iconUri = "",
        sortingPriority = 5,
    ),
    Category(
        id = 6,
        name = "Ingredients & Spices",
        iconUri = "",
        sortingPriority = 6,
    ),
    Category(
        id = 7,
        name = "Frozen & Convenience",
        iconUri = "",
        sortingPriority = 7,
    ),
    Category(
        id = 8,
        name = "Grain Products",
        iconUri = "",
        sortingPriority = 8,
    ),
    Category(
        id = 9,
        name = "Drinks",
        iconUri = "",
        sortingPriority = 9,
    ),
    Category(
        id = 10,
        name = "Snacks & Desserts",
        iconUri = "",
        sortingPriority = 10,
    ),
    Category(
        id = 11,
        name = "Health & Beauty",
        iconUri = "",
        sortingPriority = 11,
    ),
    Category(
        id = 12,
        name = "Pet Supplies",
        iconUri = "",
        sortingPriority = 12,
    ),
    Category(
        id = 13,
        name = "Household & Cleaning",
        iconUri = "",
        sortingPriority = 13,
    ),
    Category(
        id = 14,
        name = "Custom",
        iconUri = "",
        sortingPriority = 14,
    )
)

val sampleGroceryList = GroceryList(
    id = 1,
    name = "Sample Grocery List",
)

val sampleProductList = listOf(
    Product(
        id = 1,
        name = "Apples",
        iconUri = "",
        categoryId = 1,
        deletable = false,
    ),
    Product(
        id = 2,
        name = "Bread",
        iconUri = "",
        categoryId = 2,
        deletable = false,
    ),
    Product(
        id = 3,
        name = "Milk",
        iconUri = "",
        categoryId = 3,
        deletable = false,
    ),
    Product(
        id = 4,
        name = "Chicken",
        iconUri = "",
        categoryId = 4,
        deletable = false,
    ),
    Product(
        id = 5,
        name = "Fish",
        iconUri = "",
        categoryId = 5,
        deletable = false,
    )
)

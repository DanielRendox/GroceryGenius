package com.rendox.grocerygenius.data.model

import com.rendox.grocerygenius.database.grocery_list.GroceryListEntity
import com.rendox.grocerygenius.model.GroceryList

fun GroceryListEntity.asExternalModel() = GroceryList(
    id = id,
    name = name,
    sortingPriority = sortingPriority,
)

fun GroceryList.asEntity() = GroceryListEntity(
    id = id,
    name = name,
    sortingPriority = sortingPriority,
)
package com.rendox.grocerygenius.data.model

import com.rendox.grocerygenius.database.category.CategoryEntity
import com.rendox.grocerygenius.model.Category

fun CategoryEntity.asExternalModel() = Category(
    id = id,
    name = name,
    iconUri = iconUri,
    sortingPriority = sortingPriority,
    isDefault = isDefault,
)

fun Category.asEntity() = CategoryEntity(
    id = id,
    name = name,
    iconUri = iconUri,
    sortingPriority = sortingPriority,
    isDefault = isDefault,
)
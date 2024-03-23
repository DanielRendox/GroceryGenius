package com.rendox.grocerygenius.data.model

import com.rendox.grocerygenius.database.grocery.CombinedGrocery
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Grocery

fun CombinedGrocery.asExternalModel() = Grocery(
    productId = productId,
    name = name,
    purchased = purchased,
    description = description,
    iconUri = iconUri,
    category = Category(
        id = categoryId,
        name = categoryName,
        iconUri = categoryIconUri,
        sortingPriority = categorySortingPriority,
        isDefault = categoryIsDefault,
    ),
    purchasedLastModified = purchasedLastModified,
)
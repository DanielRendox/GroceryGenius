package com.rendox.grocerygenius.data.model

import com.rendox.grocerygenius.database.grocery.CombinedGrocery
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.model.Icon

fun CombinedGrocery.asExternalModel() = Grocery(
    productId = productId,
    name = name,
    purchased = purchased,
    description = description,
    icon = icon,
    category = category,
    purchasedLastModified = purchasedLastModified,
)

val CombinedGrocery.icon
    get() = when {
        iconId != null && iconFilePath != null -> Icon(
            id = iconId,
            filePath = iconFilePath,
        )

        else -> null
    }

val CombinedGrocery.category
    get() = when {
        categoryId != null
                && categoryName != null
                && categorySortingPriority != null
                && categoryIsDefault != null -> Category(
            id = categoryId,
            name = categoryName,
            sortingPriority = categorySortingPriority,
        )

        else -> null
    }
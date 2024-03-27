package com.rendox.grocerygenius.data.model

import android.graphics.Bitmap
import com.rendox.grocerygenius.database.grocery.CombinedGrocery
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Grocery

fun CombinedGrocery.asExternalModel(groceryIcon: Bitmap?) = Grocery(
    productId = productId,
    name = name,
    purchased = purchased,
    description = description,
    icon = groceryIcon,
    iconId = iconId,
    category = if (categoryId != null && categoryName != null) {
        Category(
            id = categoryId,
            name = categoryName,
            sortingPriority = categorySortingPriority,
            isDefault = categoryIsDefault,
        )
    } else null,
    purchasedLastModified = purchasedLastModified,
)
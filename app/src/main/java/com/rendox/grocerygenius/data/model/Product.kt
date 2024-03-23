package com.rendox.grocerygenius.data.model

import com.rendox.grocerygenius.database.product.CombinedProduct
import com.rendox.grocerygenius.database.product.ProductEntity
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Product

fun Product.asEntity() = ProductEntity(
    id = id,
    name = name,
    categoryId = category.id,
    iconUri = iconUri,
    deletable = deletable,
)

fun CombinedProduct.asExternalModel() = Product(
    id = id,
    name = name,
    iconUri = iconUri,
    category = Category(
        id = categoryId,
        name = categoryName,
        iconUri = categoryIconUri,
        sortingPriority = categorySortingPriority,
        isDefault = categoryIsDefault,
    ),
    deletable = deletable,
)
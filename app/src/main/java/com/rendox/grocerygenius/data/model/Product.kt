package com.rendox.grocerygenius.data.model

import com.rendox.grocerygenius.database.product.ProductEntity
import com.rendox.grocerygenius.model.Product

fun Product.asEntity() = ProductEntity(
    id = id,
    name = name,
    categoryId = categoryId,
    iconUri = iconUri,
    deletable = deletable,
)
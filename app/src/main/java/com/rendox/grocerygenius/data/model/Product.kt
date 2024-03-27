package com.rendox.grocerygenius.data.model

import android.graphics.Bitmap
import com.rendox.grocerygenius.database.product.CombinedProduct
import com.rendox.grocerygenius.database.product.ProductEntity
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Product
import com.rendox.grocerygenius.network.model.ProductNetwork

fun Product.asEntity() = ProductEntity(
    id = id,
    name = name,
    categoryId = category?.id,
    iconId = iconId,
    deletable = deletable,
)

fun CombinedProduct.asExternalModel(productIcon: Bitmap?) = Product(
    id = id,
    name = name,
    iconId = iconId,
    icon = productIcon,
    category = categoryId?.let {
        Category(
            id = it,
            name = categoryName,
            sortingPriority = categorySortingPriority,
            isDefault = categoryIsDefault,
        )
    },
    deletable = deletable,
)

fun ProductNetwork.asEntity() = ProductEntity(
    id = id,
    name = name,
    categoryId = categoryId,
    iconId = iconId,
    deletable = deletable,
)
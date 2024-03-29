package com.rendox.grocerygenius.data.model

import com.rendox.grocerygenius.database.grocery_icon.IconEntity
import com.rendox.grocerygenius.model.Icon

fun Icon.asEntity() = IconEntity(
    id = id,
    filePath = filePath,
)
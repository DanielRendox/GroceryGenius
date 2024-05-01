package com.rendox.grocerygenius.data.model

import com.rendox.grocerygenius.database.grocery_icon.IconEntity
import com.rendox.grocerygenius.model.IconReference

fun IconReference.asEntity() = IconEntity(
    uniqueFileName = uniqueFileName,
    filePath = filePath,
)
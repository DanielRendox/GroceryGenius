package com.rendox.grocerygenius.network.icons

import com.rendox.grocerygenius.model.Icon

interface IconNetworkDataSource {
    suspend fun downloadIcons(): List<Icon>
}
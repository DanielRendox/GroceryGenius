package com.rendox.grocerygenius.network.icons

import com.rendox.grocerygenius.model.Icon
import com.rendox.grocerygenius.network.model.NetworkChangeList

interface IconNetworkDataSource {
    suspend fun downloadIcons(): List<Icon>
    suspend fun downloadIconsByIds(ids: List<Int>): List<Icon>
    suspend fun getIconChangeList(after: Int): List<NetworkChangeList>
}
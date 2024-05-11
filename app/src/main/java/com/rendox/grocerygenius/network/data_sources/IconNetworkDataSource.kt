package com.rendox.grocerygenius.network.data_sources

import com.rendox.grocerygenius.model.IconReference
import com.rendox.grocerygenius.network.model.NetworkChangeList

interface IconNetworkDataSource {
    suspend fun downloadIcons(): List<IconReference>
    suspend fun downloadIconsByIds(ids: List<String>): List<IconReference>
    suspend fun getIconChangeList(after: Int): List<NetworkChangeList>
}
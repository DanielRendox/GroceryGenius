package com.rendox.grocerygenius.network.icons

interface IconNetworkDataSource {
    suspend fun downloadIcon(iconId: Int): String
}
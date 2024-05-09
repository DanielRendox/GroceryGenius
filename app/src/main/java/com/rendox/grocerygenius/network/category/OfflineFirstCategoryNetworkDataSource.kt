package com.rendox.grocerygenius.network.category

import com.rendox.grocerygenius.network.GitHubApi
import com.rendox.grocerygenius.network.model.CategoryNetwork
import com.rendox.grocerygenius.network.model.NetworkChangeList
import javax.inject.Inject


class OfflineFirstCategoryNetworkDataSource @Inject constructor(
    private val gitHubApi: GitHubApi,
) : CategoryNetworkDataSource {
    override suspend fun getAllCategories(): List<CategoryNetwork> =
        gitHubApi.getCategories()

    override suspend fun getCategoriesByIds(ids: List<String>): List<CategoryNetwork> =
        getAllCategories().filter { it.id in ids }

    override suspend fun getCategoryChangeList(after: Int): List<NetworkChangeList> =
        gitHubApi.getCategoriesChangeList()
}
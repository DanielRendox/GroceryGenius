package com.rendox.grocerygenius.network

import com.rendox.grocerygenius.network.model.CategoryNetwork
import com.rendox.grocerygenius.network.model.NetworkChangeList
import com.rendox.grocerygenius.network.model.ProductNetwork
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface GitHubApi {

    @GET("category/categories_{languageCode}.json")
    suspend fun getCategories(
        @Path("languageCode") languageCode: String = "en",
    ): List<CategoryNetwork>

    @GET("category/categories_change_list.json")
    suspend fun getCategoriesChangeList(): List<NetworkChangeList>

    @GET("icons/all_icons.zip")
    @Streaming
    suspend fun getIconsArchive(): ResponseBody

    @GET("icons/{iconName}")
    suspend fun getIconByName(iconName: String): ResponseBody

    @GET("icons/icons_change_list.json")
    suspend fun getIconChangeList(): List<NetworkChangeList>

    @GET("product/default_products_{languageCode}.json")
    suspend fun getProducts(
        @Path("languageCode") languageCode: String = "en",
    ): List<ProductNetwork>

    @GET("product/default_products_change_list.json")
    suspend fun getProductsChangeList(): List<NetworkChangeList>
}

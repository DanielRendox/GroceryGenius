package com.rendox.grocerygenius.network.di

import com.rendox.grocerygenius.network.category.CategoryNetworkDataSource
import com.rendox.grocerygenius.network.category.OfflineCategoryNetworkDataSource
import com.rendox.grocerygenius.network.grocery_list.GroceryListNetworkDataSource
import com.rendox.grocerygenius.network.grocery_list.OfflineGroceryListNetworkDataSourceFake
import com.rendox.grocerygenius.network.product.OfflineProductNetworkDataSource
import com.rendox.grocerygenius.network.product.ProductNetworkDataSource
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindCategoryNetworkDataSource(
        categoryNetworkDataSource: OfflineCategoryNetworkDataSource
    ): CategoryNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindGroceryListNetworkDataSource(
        groceryNetworkDataSource: OfflineGroceryListNetworkDataSourceFake
    ): GroceryListNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindProductNetworkDataSource(
        productNetworkDataSource: OfflineProductNetworkDataSource
    ): ProductNetworkDataSource

    companion object {
        @Provides
        @Singleton
        fun provideMoshi(): Moshi = Moshi.Builder().build()
    }
}
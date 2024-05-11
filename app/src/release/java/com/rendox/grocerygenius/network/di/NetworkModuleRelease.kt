package com.rendox.grocerygenius.network.di

import com.rendox.grocerygenius.network.category.CategoryNetworkDataSource
import com.rendox.grocerygenius.network.category.OfflineFirstCategoryNetworkDataSource
import com.rendox.grocerygenius.network.icons.IconNetworkDataSource
import com.rendox.grocerygenius.network.icons.OfflineFirstIconNetworkDataSource
import com.rendox.grocerygenius.network.product.OfflineFirstProductNetworkDataSource
import com.rendox.grocerygenius.network.product.ProductNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModuleRelease {

    @Binds
    @Singleton
    abstract fun bindCategoryNetworkDataSource(
        categoryNetworkDataSource: OfflineFirstCategoryNetworkDataSource
    ): CategoryNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindProductNetworkDataSource(
        productNetworkDataSource: OfflineFirstProductNetworkDataSource
    ): ProductNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindIconNetworkDataSource(
        iconNetworkDataSource: OfflineFirstIconNetworkDataSource
    ): IconNetworkDataSource
}
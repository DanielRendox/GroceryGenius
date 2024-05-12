package com.rendox.grocerygenius.network.di

import com.rendox.grocerygenius.network.data_sources.OfflineFirstCategoryNetworkDataSource
import com.rendox.grocerygenius.network.data_sources.CategoryNetworkDataSource
import com.rendox.grocerygenius.network.data_sources.IconNetworkDataSource
import com.rendox.grocerygenius.network.data_sources.ProductNetworkDataSource
import com.rendox.grocerygenius.network.data_sources.OfflineFirstIconNetworkDataSource
import com.rendox.grocerygenius.network.data_sources.OfflineFirstProductNetworkDataSource
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
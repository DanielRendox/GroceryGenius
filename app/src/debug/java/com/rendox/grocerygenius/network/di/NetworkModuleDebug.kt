package com.rendox.grocerygenius.network.di

import com.rendox.grocerygenius.network.data_sources.CategoryNetworkDataSource
import com.rendox.grocerygenius.network.data_sources.FakeCategoryNetworkDataSource
import com.rendox.grocerygenius.network.data_sources.IconNetworkDataSource
import com.rendox.grocerygenius.network.data_sources.FakeIconNetworkDataSource
import com.rendox.grocerygenius.network.data_sources.FakeProductNetworkDataSource
import com.rendox.grocerygenius.network.data_sources.ProductNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModuleDebug {

    @Binds
    @Singleton
    abstract fun bindCategoryNetworkDataSource(
        categoryNetworkDataSource: FakeCategoryNetworkDataSource
    ): CategoryNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindProductNetworkDataSource(
        productNetworkDataSource: FakeProductNetworkDataSource
    ): ProductNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindIconNetworkDataSource(
        iconNetworkDataSource: FakeIconNetworkDataSource
    ): IconNetworkDataSource
}
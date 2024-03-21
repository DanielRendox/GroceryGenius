package com.rendox.grocerygenius.network.di

import com.rendox.grocerygenius.network.category.CategoryNetworkDataSource
import com.rendox.grocerygenius.network.category.CategoryNetworkDataSourceFake
import com.rendox.grocerygenius.network.grocery_list.GroceryListNetworkDataSource
import com.rendox.grocerygenius.network.grocery_list.GroceryListNetworkDataSourceFake
import com.rendox.grocerygenius.network.product.ProductNetworkDataSource
import com.rendox.grocerygenius.network.product.ProductNetworkDataSourceFake
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindCategoryNetworkDataSource(
        categoryNetworkDataSource: CategoryNetworkDataSourceFake
    ): CategoryNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindGroceryListNetworkDataSource(
        groceryNetworkDataSource: GroceryListNetworkDataSourceFake
    ): GroceryListNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindProductNetworkDataSource(
        productNetworkDataSource: ProductNetworkDataSourceFake
    ): ProductNetworkDataSource
}
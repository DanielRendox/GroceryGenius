package com.rendox.grocerygenius.data

import com.rendox.grocerygenius.data.category.CategoryRepository
import com.rendox.grocerygenius.data.category.CategoryRepositoryImpl
import com.rendox.grocerygenius.data.grocery.GroceryRepository
import com.rendox.grocerygenius.data.grocery.GroceryRepositoryImpl
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepositoryImpl
import com.rendox.grocerygenius.data.product.ProductRepository
import com.rendox.grocerygenius.data.product.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun groceryRepository(
        groceryRepositoryImpl: GroceryRepositoryImpl
    ): GroceryRepository

    @Binds
    @Singleton
    abstract fun bindGroceryListRepository(
        groceryListRepositoryImpl: GroceryListRepositoryImpl
    ): GroceryListRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository
}
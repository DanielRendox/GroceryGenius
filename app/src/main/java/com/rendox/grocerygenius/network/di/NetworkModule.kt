package com.rendox.grocerygenius.network.di

import androidx.multidex.BuildConfig
import com.rendox.grocerygenius.network.GitHubApi
import com.rendox.grocerygenius.network.category.CategoryNetworkDataSource
import com.rendox.grocerygenius.network.category.OfflineFirstCategoryNetworkDataSource
import com.rendox.grocerygenius.network.icons.IconNetworkDataSource
import com.rendox.grocerygenius.network.icons.OfflineFirstIconNetworkDataSource
import com.rendox.grocerygenius.network.product.OfflineFirstProductNetworkDataSource
import com.rendox.grocerygenius.network.product.ProductNetworkDataSource
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

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

    companion object {
        @Provides
        @Singleton
        fun provideMoshi(): Moshi = Moshi.Builder().build()

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    this.addNetworkInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        },
                    )
                }
            }
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestWithHeaders = originalRequest.newBuilder()
                    .header("Accept", "application/vnd.github.raw+json")
                    .build()
                chain.proceed(requestWithHeaders)
            }
            .build()

        @Provides
        @Singleton
        fun provideGitHubApi(okHttpClient: OkHttpClient): GitHubApi = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.github.com/repos/DanielRendox/GroceryGenius/contents/assets/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)
    }
}
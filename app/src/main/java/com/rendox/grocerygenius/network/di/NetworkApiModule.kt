package com.rendox.grocerygenius.network.di

import com.rendox.grocerygenius.BuildConfig
import com.rendox.grocerygenius.network.GitHubApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkApiModule {
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
                        level = HttpLoggingInterceptor.Level.HEADERS
                    },
                )
            }
        }
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val urlWithQueryParam = originalRequest.url.newBuilder()
                .addQueryParameter("ref", "production")
                .build()
            val requestWithHeadersAndQueryParam = originalRequest.newBuilder()
                .header("Accept", "application/vnd.github.raw+json")
                .url(urlWithQueryParam)
                .build()
            chain.proceed(requestWithHeadersAndQueryParam)
        }
        .readTimeout(20, TimeUnit.SECONDS)
        .connectTimeout(20, TimeUnit.SECONDS)
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
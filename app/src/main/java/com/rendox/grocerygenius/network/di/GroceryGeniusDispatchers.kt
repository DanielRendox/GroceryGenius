package com.rendox.grocerygenius.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: GroceryGeniusDispatchers)

enum class GroceryGeniusDispatchers {
    Default,
    IO,
}
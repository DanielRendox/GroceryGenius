package com.rendox.grocerygenius.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val niaDispatcher: GroceryGeniusDispatchers)

enum class GroceryGeniusDispatchers {
    Default,
    IO,
}
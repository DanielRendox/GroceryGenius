package com.rendox.grocerygenius.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

inline fun <reified T> Moshi.listAdapter(): JsonAdapter<List<T>> {
    val type = Types.newParameterizedType(List::class.java, T::class.java)
    return adapter(type)
}

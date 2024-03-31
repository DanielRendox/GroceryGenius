package com.rendox.grocerygenius.datastore

import androidx.datastore.preferences.core.intPreferencesKey

object PreferencesKeys {
    val IconVersion = intPreferencesKey("icon_version")
    val CategoryVersion = intPreferencesKey("category_version")
    val ProductVersion = intPreferencesKey("product_version")
}
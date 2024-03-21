package com.rendox.grocerygenius

import android.app.Application
import com.rendox.grocerygenius.sync.work.initializers.Sync
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GroceryGeniusApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Sync; the system responsible for keeping data in the app up to date.
        Sync.initialize(context = this)
    }
}
package com.rendox.grocerygenius.feature.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.rendox.grocerygenius.ui.GroceryGeniusTransition

const val SETTINGS_ROUTE = "settings_route"

fun NavController.navigateToSettings(
    navOptions: (NavOptionsBuilder.() -> Unit) = {},
) {
    this.navigate(
        route = SETTINGS_ROUTE,
        builder = navOptions,
    )
}

fun NavGraphBuilder.settingsScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = SETTINGS_ROUTE,
        enterTransition = { GroceryGeniusTransition.SlideInVertically },
        exitTransition = { GroceryGeniusTransition.SlideOutVertically },
    ) {
        SettingsRoute(
            navigateBack = navigateBack,
        )
    }
}
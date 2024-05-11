package com.rendox.grocerygenius.feature.onboarding

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rendox.grocerygenius.ui.GroceryGeniusTransition

const val ONBOARDING_ROUTE = "onboarding_route"

fun NavGraphBuilder.onboardingScreen(
    closeOnboarding: () -> Unit
) {
    composable(
        route = ONBOARDING_ROUTE,
        enterTransition = { GroceryGeniusTransition.SlideInVertically },
        exitTransition = { GroceryGeniusTransition.SlideOutVertically },
    ) {
        OnboardingSyncRoute(
            closeOnboarding = closeOnboarding,
        )
    }
}
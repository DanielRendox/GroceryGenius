package com.rendox.grocerygenius.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

/**

 */
object GroceryGeniusTransition {

    /*
     * The code of sharedZ animation was taken from https://github.com/ofalvai/HabitBuilder
     * Thank you @ofalvai!
     */
    val sharedZAxisEnterForward = scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(durationMillis = 60, delayMillis = 60, easing = LinearEasing))
    val sharedZAxisEnterBackward = scaleIn(
        initialScale = 1.1f,
        animationSpec = tween(300)
    )
    val sharedZAxisExitBackward = scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(durationMillis = 300),
    ) + fadeOut(animationSpec = tween(durationMillis = 60, delayMillis = 60, easing = LinearEasing))
}
package com.rendox.grocerygenius.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

/**

 */
object GroceryGeniusTransition {

    /*
   * The code of sharedZ animation was taken from https://github.com/ofalvai/HabitBuilder
   * Thank you @ofalvai!
   */
    val SharedZAxisEnterForward = scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(durationMillis = 60, delayMillis = 60, easing = LinearEasing))
    val SharedZAxisEnterBackward = scaleIn(
        initialScale = 1.1f,
        animationSpec = tween(300)
    )
    val SharedZAxisExitBackward = scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(durationMillis = 300),
    ) + fadeOut(animationSpec = tween(durationMillis = 60, delayMillis = 60, easing = LinearEasing))

    val SlideInVertically = slideInVertically(
        initialOffsetY = { it / 10 },
        animationSpec = tween(durationMillis = 200),
    ) + fadeIn(animationSpec = tween(durationMillis = 150, easing = LinearEasing))
    val SlideOutVertically = slideOutVertically(
        targetOffsetY = { it / 10 },
        animationSpec = tween(durationMillis = 200),
    ) + fadeOut(animationSpec = tween(durationMillis = 150, easing = LinearEasing))

    val SlideInHorizontallyEnterForward = slideInHorizontally { it }
    val SlideInHorizontallyEnterBackward = slideInHorizontally { -it }
    val SlideOutHorizontallyExitForward = slideOutHorizontally { it }
    val SlideOutHorizontallyExitBackward = slideOutHorizontally { -it }
}
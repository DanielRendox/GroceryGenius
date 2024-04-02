package com.rendox.grocerygenius.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val redAccent: Color,
    val onRedAccent: Color,
)

val LightExtendedColors = ExtendedColors(
    redAccent = md_theme_light_red_accent,
    onRedAccent = md_theme_light_onRedAccent,
)

val DarkExtendedColors = ExtendedColors(
    redAccent = md_theme_dark_red_accent,
    onRedAccent = md_theme_dark_onRedAccent,
)

val LocalExtendedColors = compositionLocalOf { LightExtendedColors }

@Suppress("UnusedReceiverParameter")
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current
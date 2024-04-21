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
    redAccent = Color(0xFFDD5B4B),
    onRedAccent = Color(0xFFFFFFFF),
)

val DarkExtendedColors = ExtendedColors(
    redAccent = Color(0xFFFF988D),
    onRedAccent = Color(0xFF490003),
)

val LocalExtendedColors = compositionLocalOf { LightExtendedColors }

@Suppress("UnusedReceiverParameter")
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current
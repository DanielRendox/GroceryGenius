package com.rendox.grocerygenius.ui.components.collapsing_toolbar

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.ToolbarState
import kotlin.math.roundToInt

private val collapsedTitleFontSize = 22.sp

@Composable
fun CollapsingToolbar(
    modifier: Modifier = Modifier,
    toolbarState: ToolbarState,
    toolbarHeightRange: IntRange,
    titleExpanded: @Composable () -> Unit,
    expandedTitleFontSize: TextUnit,
    titleBottomPadding: Dp,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    val toolbarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
        elevation = lerp(
            start = 3.dp,
            stop = 0.dp,
            fraction = toolbarState.progress,
        )
    )

    val dynamicToolbarHeight = with(LocalDensity.current) {
        toolbarState.height.toDp()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(color = toolbarColor)
            }
            .systemBarsPadding()
            .height(dynamicToolbarHeight)
    ) {
        MaterialTheme.typography.headlineMedium
        val textCompressionRatio = collapsedTitleFontSize.value / expandedTitleFontSize.value
        CollapsingTextLayout(
            progress = toolbarState.progress,
            textCompressionRatio = textCompressionRatio,
            titleBottomPadding = titleBottomPadding,
        ) {

            val textSizeWithoutCompression = 1F
            val textSize: Float = lerp(
                start = textCompressionRatio,
                stop = textSizeWithoutCompression,
                fraction = toolbarState.progress,
            )

            val textDefaultEndPadding = 16.dp
            val textCompressedEndPadding = when {
                actions == null -> 0.dp
                navigationIcon == null -> 30.dp
                else -> 100.dp
            }
            val textEndPadding: Dp = lerp(
                start = textCompressedEndPadding,
                stop = textDefaultEndPadding,
                fraction = toolbarState.progress,
            )

            val titleStartPadding = when (LocalConfiguration.current.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> 24.dp
                else -> 16.dp
            }

            val textStartPadding = if (navigationIcon == null) titleStartPadding else lerp(
                start = 0.dp,
                stop = titleStartPadding,
                fraction = toolbarState.progress,
            )

            Box(
                modifier = Modifier
                    .padding(
                        end = textEndPadding,
                        start = textStartPadding,
                    )
                    .graphicsLayer(
                        scaleX = textSize,
                        scaleY = textSize,
                        transformOrigin = TransformOrigin(0F, 0F),
                    ),
            ) {
                titleExpanded()
            }

            val collapsedHeight = with(LocalDensity.current) {
                toolbarHeightRange.first.toDp()
            }

            if (navigationIcon != null) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(collapsedHeight),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    navigationIcon()
                }
            } else {
                Spacer(modifier = Modifier)
            }

            if (actions != null) {
                Box(
                    modifier = Modifier
                        .height(collapsedHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Row(content = actions)
                }
            } else {
                Spacer(modifier = Modifier)
            }
        }
    }
}

@Composable
private fun CollapsingTextLayout(
    modifier: Modifier = Modifier,
    progress: Float,
    titleBottomPadding: Dp,
    textCompressionRatio: Float,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        val placeables = measurables.map {
            it.measure(constraints)
        }

        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight,
        ) {
            val title = placeables[0]
            val navigationIcon = placeables[1]
            val actions = placeables[2]

            val compressedTextHeight: Float = title.height * textCompressionRatio
            val centeredTitlePosition =
                ((constraints.maxHeight - compressedTextHeight) * 0.5F).roundToInt()
            val bottomTitlePosition =
                constraints.maxHeight - title.height - titleBottomPadding.roundToPx()
            title.placeRelative(
                x = lerp(
                    start = navigationIcon.width,
                    stop = 0,
                    fraction = progress,
                ),
                y = lerp(
                    start = centeredTitlePosition,
                    stop = bottomTitlePosition,
                    fraction = progress,
                ),
            )

            navigationIcon.placeRelative(
                x = 0,
                y = 0,
            )

            actions.placeRelative(
                x = 0,
                y = 0,
            )
        }
    }
}

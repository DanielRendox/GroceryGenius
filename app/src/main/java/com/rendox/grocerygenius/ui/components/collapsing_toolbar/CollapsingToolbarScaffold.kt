package com.rendox.grocerygenius.ui.components.collapsing_toolbar

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.CollapsingToolbarNestedScrollConnection
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.FixedScrollFlagState
import kotlinx.coroutines.cancelChildren

@Composable
fun CollapsingToolbarScaffold(
    modifier: Modifier = Modifier,
    nestedScrollConnection: CollapsingToolbarNestedScrollConnection,
    toolbar: @Composable () -> Unit,
    toolbarHeightRange: IntRange,
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable (bottomPadding: Dp) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        contentColor = contentColor,
    ) { paddingValues ->
        val toolbarState = nestedScrollConnection.toolbarState
        Box(
            modifier = Modifier
                .nestedScroll(nestedScrollConnection)
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = toolbarState.height + toolbarState.offset
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                nestedScrollConnection.coroutineScope.coroutineContext.cancelChildren()
                            }
                        )
                    }
            ) {
                val collapsedToolbarHeight = with(LocalDensity.current) {
                    toolbarHeightRange.first.toDp()
                }
                val bottomPadding =
                    if (toolbarState is FixedScrollFlagState) collapsedToolbarHeight else 0.dp
                content(bottomPadding)
            }
            Box(
                modifier = Modifier.graphicsLayer {
                    translationY = toolbarState.offset
                }
            ) {
                toolbar()
            }
        }
    }
}
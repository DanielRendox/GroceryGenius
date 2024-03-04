package com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior

import androidx.compose.animation.core.FloatExponentialDecaySpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbarScaffoldScrollableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class CollapsingToolbarNestedScrollConnection(
    val toolbarState: ToolbarState,
    private val scrollState: CollapsingToolbarScaffoldScrollableState,
    val coroutineScope: CoroutineScope,
) : NestedScrollConnection {
    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        toolbarState.scrollTopLimitReached =
            scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset == 0
        toolbarState.scrollOffset = toolbarState.scrollOffset - available.y
        return Offset(0f, toolbarState.consumed)
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity,
    ): Velocity {
        if (available.y > 0) {
            coroutineScope.launch {
                animateDecay(
                    initialValue = toolbarState.height + toolbarState.offset,
                    initialVelocity = available.y,
                    animationSpec = FloatExponentialDecaySpec()
                ) { value, _ ->
                    toolbarState.scrollTopLimitReached =
                        scrollState.firstVisibleItemIndex == 0 &&
                                scrollState.firstVisibleItemScrollOffset == 0
                    toolbarState.scrollOffset =
                        toolbarState.scrollOffset -
                                (value - (toolbarState.height + toolbarState.offset))
                    if (toolbarState.scrollOffset == 0f) coroutineScope.coroutineContext.cancelChildren()
                }
            }
        }

        return super.onPostFling(consumed, available)
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        toolbarState.scrollTopLimitReached =
            scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset == 0
        toolbarState.scrollOffset = toolbarState.scrollOffset - available.y
        return Offset(0f, toolbarState.consumed)
    }
}
package com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class ExitUntilCollapsedState(
    heightRange: IntRange,
    scrollOffset: Float = 0f
) : FixedScrollFlagState(heightRange) {

    override var _scrollOffset by mutableFloatStateOf(
        value = scrollOffset.coerceIn(0f, rangeDifference.toFloat()),
    )

    override var scrollOffset: Float
        get() = _scrollOffset
        set(value) {
            if (scrollTopLimitReached) {
                val oldOffset = _scrollOffset
                _scrollOffset = value.coerceIn(0f, rangeDifference.toFloat())
                _consumed = oldOffset - _scrollOffset
            } else {
                _consumed = 0f
            }
        }

    companion object {
        val Saver = run {

            val minHeightKey = "MinHeight"
            val maxHeightKey = "MaxHeight"
            val scrollOffsetKey = "ScrollOffset"

            mapSaver(
                save = {
                    mapOf(
                        minHeightKey to it.minHeight,
                        maxHeightKey to it.maxHeight,
                        scrollOffsetKey to it.scrollOffset
                    )
                },
                restore = {
                    ExitUntilCollapsedState(
                        heightRange = (it[minHeightKey] as Int)..(it[maxHeightKey] as Int),
                        scrollOffset = it[scrollOffsetKey] as Float,
                    )
                }
            )
        }
    }
}

@Composable
fun rememberExitUntilCollapsedToolbarState(
    toolbarHeightRange: IntRange
): ExitUntilCollapsedState = rememberSaveable(saver = ExitUntilCollapsedState.Saver) {
    ExitUntilCollapsedState(toolbarHeightRange)
}
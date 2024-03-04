package com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior

abstract class FixedScrollFlagState(heightRange: IntRange) : ScrollFlagState(heightRange) {

    final override val offset: Float = 0f

}
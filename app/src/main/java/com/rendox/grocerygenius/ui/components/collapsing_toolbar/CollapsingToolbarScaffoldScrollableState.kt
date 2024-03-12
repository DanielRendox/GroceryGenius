package com.rendox.grocerygenius.ui.components.collapsing_toolbar

import androidx.compose.foundation.gestures.ScrollableState

interface CollapsingToolbarScaffoldScrollableState : ScrollableState {
    val firstVisibleItemIndex: Int
    val firstVisibleItemScrollOffset: Int
}
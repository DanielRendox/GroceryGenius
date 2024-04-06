package com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusState
import com.rendox.grocerygenius.ui.helpers.UiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class AddGroceryBottomSheetState(
    val bottomSheetState: SheetState,
    private val coroutineScope: CoroutineScope,
) {
    val sheetIsExpanding = bottomSheetState.targetValue == SheetValue.Expanded
    val sheetIsCollapsing = bottomSheetState.targetValue == SheetValue.PartiallyExpanded
    private val sheetIsFullyExpanded = bottomSheetState.currentValue == SheetValue.Expanded

    val showCancelButtonInsteadOfFab = sheetIsExpanding
    val handleBackButtonPress = sheetIsExpanding
    val useExpandedPlaceholderText = sheetIsExpanding

    var showExtendedContent = false
        private set

    var changeSearchFieldFocusEvent by mutableStateOf<UiEvent<Boolean>?>(null)
        private set

    init {
        keepSheetAlwaysOnScreen()
        val sheetIsCollapsing = !sheetIsExpanding
        when {
            sheetIsCollapsing -> {
                sendChangeSearchFieldFocusEvent(isFocused = false)
                showExtendedContent = false
            }
            sheetIsFullyExpanded -> {
                sendChangeSearchFieldFocusEvent(isFocused = true)
                showExtendedContent = true
            }
        }
    }

    private fun sendChangeSearchFieldFocusEvent(isFocused: Boolean) {
        changeSearchFieldFocusEvent = object : UiEvent<Boolean> {
            override val data = isFocused
            override fun onConsumed() {
                changeSearchFieldFocusEvent = null
            }
        }
    }

    fun onSearchFieldFocusChanged(focusState: FocusState) {
        if (focusState.isFocused) {
            coroutineScope.launch {
                bottomSheetState.expand()
            }
        }
    }

    fun onFabClicked() = expandSheet()
    fun onCancelButtonClicked() = collapseSheet()
    fun onSheetDismissed() = collapseSheet()
    fun onKeyboardDone() = collapseSheet()

    private fun expandSheet() {
        coroutineScope.launch {
            bottomSheetState.expand()
        }
    }

    private fun collapseSheet() {
        coroutineScope.launch {
            bottomSheetState.partialExpand()
        }
    }

    private fun keepSheetAlwaysOnScreen() {
        if (bottomSheetState.targetValue == SheetValue.Hidden) {
            expandSheet()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAddGroceryBottomSheetState(
    bottomSheetState: SheetState = rememberStandardBottomSheetState(),
): AddGroceryBottomSheetState {
    val coroutineScope = rememberCoroutineScope()
    return remember(
        bottomSheetState.currentValue,
        bottomSheetState.targetValue,
    ) {
        AddGroceryBottomSheetState(
            bottomSheetState = bottomSheetState,
            coroutineScope = coroutineScope,
        )
    }
}
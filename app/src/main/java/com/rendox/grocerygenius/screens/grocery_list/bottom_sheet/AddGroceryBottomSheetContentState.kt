package com.rendox.grocerygenius.screens.grocery_list.bottom_sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusState
import com.rendox.grocerygenius.ui.helpers.UiEvent

class AddGroceryBottomSheetContentState(
    searchInput: String = "",
    showCancelButtonInsteadOfFab: Boolean = false,
    useExpandedPlaceholderText: Boolean = false,
    clearSearchInputButtonIsShown: Boolean = false,
) {
    var searchInput by mutableStateOf(searchInput)
        private set

    var clearSearchInputButtonIsShown by mutableStateOf(clearSearchInputButtonIsShown)
        private set

    var expandBottomSheetEvent by mutableStateOf<UiEvent<Boolean>?>(null)
        private set

    var showCancelButtonInsteadOfFab by mutableStateOf(showCancelButtonInsteadOfFab)
    var useExpandedPlaceholderText by mutableStateOf(useExpandedPlaceholderText)

    fun clearSearchInput() {
        searchInput = ""
        clearSearchInputButtonIsShown = false
    }

    fun updateSearchInput(newSearchInput: String) {
        searchInput = newSearchInput
        clearSearchInputButtonIsShown = searchInput.isNotEmpty()
    }

    fun onSearchBarFocusChanged(focusState: FocusState) {
        if (focusState.isFocused) {
            sendExpandBottomSheetEvent(true)
        }
    }

    fun fabOnClick() {
        sendExpandBottomSheetEvent(true)
    }

    fun cancelButtonOnClick() {
        sendExpandBottomSheetEvent(false)
    }

    fun onKeyboardDone() {
        sendExpandBottomSheetEvent(false)
    }

    private fun sendExpandBottomSheetEvent(expand: Boolean) {
        expandBottomSheetEvent = object : UiEvent<Boolean> {
            override val data: Boolean = expand
            override fun onConsumed() {
                expandBottomSheetEvent = null
            }
        }
    }

    companion object {
        val Saver: Saver<AddGroceryBottomSheetContentState, *> = listSaver(
            save = { bottomSheetContentState ->
                listOf(
                    bottomSheetContentState.searchInput,
                    bottomSheetContentState.clearSearchInputButtonIsShown,
                )
            },
            restore = { setGoalScreenStateValues ->
                AddGroceryBottomSheetContentState(
                    searchInput = setGoalScreenStateValues[0] as String,
                    clearSearchInputButtonIsShown = setGoalScreenStateValues[1] as Boolean,
                )
            }
        )
    }
}

@Composable
fun rememberAddGroceryBottomSheetContentState(): AddGroceryBottomSheetContentState {
    return rememberSaveable(saver = AddGroceryBottomSheetContentState.Saver) {
        AddGroceryBottomSheetContentState()
    }
}


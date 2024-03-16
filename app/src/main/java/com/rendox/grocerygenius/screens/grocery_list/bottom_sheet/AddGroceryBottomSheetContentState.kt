package com.rendox.grocerygenius.screens.grocery_list.bottom_sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusState
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.ui.helpers.UiEvent

class AddGroceryBottomSheetContentState(
    searchInput: String = "",
    showCancelButtonInsteadOfFab: Boolean = false,
    useExpandedPlaceholderText: Boolean = false,
    clearSearchInputButtonIsShown: Boolean = false,
    contentType: BottomSheetContentType = BottomSheetContentType.HeaderOnly,
    itemDescription: String? = null,
    clearItemDescriptionButtonIsShown: Boolean = false,
    previousGroceryId: Int? = null,
    previousGroceryName: String? = null,
) {
    var searchInput by mutableStateOf(searchInput)
        private set

    var clearSearchInputButtonIsShown by mutableStateOf(clearSearchInputButtonIsShown)
        private set

    var expandBottomSheetEvent by mutableStateOf<UiEvent<Boolean>?>(null)
        private set

    var contentType by mutableStateOf(contentType)
        private set

    var itemDescription by mutableStateOf(itemDescription)
        private set

    var clearItemDescriptionButtonIsShown by mutableStateOf(clearItemDescriptionButtonIsShown)
        private set

    var previousGroceryId by mutableStateOf(previousGroceryId)
        private set

    var previousGroceryName by mutableStateOf(previousGroceryName)
        private set

    var sheetIsExpanding = false
        set(value) {
            field = value
            showCancelButtonInsteadOfFab = value
            useExpandedPlaceholderText = value
            val sheetIsCollapsing = !value
            if (sheetIsCollapsing && contentType == BottomSheetContentType.RefineItemOptions) {
                contentType = BottomSheetContentType.HeaderOnly
            }
        }

    var sheetIsCollapsed = true
        set(value) {
            field = value
            contentType = if (value) {
                BottomSheetContentType.HeaderOnly
            } else {
                BottomSheetContentType.Suggestions
            }
        }

    var showCancelButtonInsteadOfFab by mutableStateOf(sheetIsExpanding)
        private set

    var useExpandedPlaceholderText by mutableStateOf(sheetIsExpanding)
        private set

    fun clearSearchInput() {
        searchInput = ""
        clearSearchInputButtonIsShown = false
    }

    fun updateSearchInput(newSearchInput: String) {
        searchInput = newSearchInput
        val searchInputIsNotEmpty = searchInput.isNotEmpty()
        clearSearchInputButtonIsShown = searchInputIsNotEmpty
        if (searchInputIsNotEmpty) contentType = BottomSheetContentType.SearchResults
    }

    fun onSearchBarFocusChanged(focusState: FocusState) {
        if (focusState.isFocused) {
            changeBottomSheetState(expand = true)
        }
    }

    fun fabOnClick() {
        changeBottomSheetState(expand = true)
    }

    fun cancelButtonOnClick() {
        changeBottomSheetState(expand = false)
    }

    fun onKeyboardDone() {
        changeBottomSheetState(expand = false)
    }

    fun onGroceryItemClick(grocery: Grocery) {
        clearSearchInput()
        previousGroceryId = grocery.id
        previousGroceryName = grocery.name
        contentType = BottomSheetContentType.RefineItemOptions
    }

    fun updateItemDescription(description: String) {
        itemDescription = description
        clearItemDescriptionButtonIsShown = description.isNotEmpty()
    }

    fun clearItemDescription() {
        itemDescription = ""
        clearItemDescriptionButtonIsShown = false
    }

    private fun changeBottomSheetState(expand: Boolean) {
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
                    bottomSheetContentState.contentType,
                    bottomSheetContentState.itemDescription,
                    bottomSheetContentState.clearItemDescriptionButtonIsShown,
                    bottomSheetContentState.previousGroceryId,
                    bottomSheetContentState.previousGroceryName,
                )
            },
            restore = { setGoalScreenStateValues ->
                AddGroceryBottomSheetContentState(
                    searchInput = setGoalScreenStateValues[0] as String,
                    clearSearchInputButtonIsShown = setGoalScreenStateValues[1] as Boolean,
                    contentType = setGoalScreenStateValues[2] as BottomSheetContentType,
                    itemDescription = setGoalScreenStateValues[3] as String?,
                    clearItemDescriptionButtonIsShown = setGoalScreenStateValues[4] as Boolean,
                    previousGroceryId = setGoalScreenStateValues[5] as Int?,
                    previousGroceryName = setGoalScreenStateValues[6] as String?,
                )
            }
        )
    }
}

@Composable
fun rememberAddGroceryBottomSheetContentState(
    searchInput: String = "",
    showCancelButtonInsteadOfFab: Boolean = false,
    useExpandedPlaceholderText: Boolean = false,
    clearSearchInputButtonIsShown: Boolean = false,
    contentType: BottomSheetContentType = BottomSheetContentType.Suggestions,
    itemDescription: String? = null,
    clearItemDescriptionButtonIsShown: Boolean = false,
    previousGroceryId: Int? = null,
    previousGroceryName: String? = null,
): AddGroceryBottomSheetContentState {
    return rememberSaveable(saver = AddGroceryBottomSheetContentState.Saver) {
        AddGroceryBottomSheetContentState(
            searchInput = searchInput,
            showCancelButtonInsteadOfFab = showCancelButtonInsteadOfFab,
            useExpandedPlaceholderText = useExpandedPlaceholderText,
            clearSearchInputButtonIsShown = clearSearchInputButtonIsShown,
            contentType = contentType,
            itemDescription = itemDescription,
            clearItemDescriptionButtonIsShown = clearItemDescriptionButtonIsShown,
            previousGroceryId = previousGroceryId,
            previousGroceryName = previousGroceryName,
        )
    }
}


package com.rendox.grocerygenius.screens.grocery_list.bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.ui.components.grocery_list.GroceryGroup
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGrid
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGridItem


@Composable
fun AddGroceryBottomSheetContent(
    modifier: Modifier = Modifier,
    onSearchInputChanged: (String) -> Unit,
    onGrocerySearchResultClick: (Grocery) -> Unit,
    grocerySearchResults: List<GroceryGroup>,
    onKeyboardDone: () -> Unit,
    state: AddGroceryBottomSheetContentState = rememberAddGroceryBottomSheetContentState(),
    searchBarFocusRequester: FocusRequester,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            SearchField(
                modifier = Modifier.weight(1F),
                searchBarFocusRequester = searchBarFocusRequester,
                searchInput = state.searchInput,
                onSearchInputChanged = { searchInput ->
                    state.updateSearchInput(searchInput)
                    onSearchInputChanged(searchInput)
                },
                onFocusChanged = state::onSearchBarFocusChanged,
                useExpandedPlaceholderText = state.useExpandedPlaceholderText,
                clearSearchInputButtonIsShown = state.clearSearchInputButtonIsShown,
                onClearSearchInputClicked = state::clearSearchInput,
                onKeyboardDone = {
                    state.onKeyboardDone()
                    onKeyboardDone()
                },
            )
            Fab(
                showCancelButtonInsteadOfFab = state.showCancelButtonInsteadOfFab,
                onCancelButtonClicked = state::cancelButtonOnClick,
                onFabClicked = state::fabOnClick,
            )
        }
        LazyGroceryGrid(
            modifier = Modifier.padding(top = 16.dp),
            groceryGroups = grocerySearchResults,
            groceryItem = { grocery ->
                LazyGroceryGridItem(
                    modifier = Modifier.fillMaxSize(),
                    purchasedColor = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp),
                    grocery = grocery,
                    onClick = {
                        onGrocerySearchResultClick(grocery)
                        state.clearSearchInput()
                    },
                )
            },
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp,
            ),
        )
    }
}

@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
    searchBarFocusRequester: FocusRequester,
    searchInput: String,
    onSearchInputChanged: (String) -> Unit,
    onFocusChanged: (FocusState) -> Unit,
    useExpandedPlaceholderText: Boolean,
    clearSearchInputButtonIsShown: Boolean,
    onClearSearchInputClicked: () -> Unit,
    onKeyboardDone: () -> Unit,
) {
    TextField(
        modifier = modifier
            .onFocusChanged(onFocusChanged = onFocusChanged)
            .focusRequester(focusRequester = searchBarFocusRequester),
        value = searchInput,
        onValueChange = onSearchInputChanged,
        placeholder = {
            Text(
                text = if (useExpandedPlaceholderText) {
                    stringResource(R.string.add_grocery_text_field_placeholder_expanded)
                } else {
                    stringResource(R.string.add_grocery_text_field_placeholder_collapsed)
                }
            )
        },
        shape = RoundedCornerShape(20),
        colors = TextFieldDefaults.colors().copy(
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = { onKeyboardDone() }
        ),
        trailingIcon = {
            val contentDescription =
                stringResource(R.string.add_grocery_text_field_trailing_icon_description)
            if (clearSearchInputButtonIsShown) {
                IconButton(onClick = onClearSearchInputClicked) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = contentDescription,
                    )
                }
            }
        }
    )
}

@Composable
private fun Fab(
    showCancelButtonInsteadOfFab: Boolean,
    onCancelButtonClicked: () -> Unit,
    onFabClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (showCancelButtonInsteadOfFab) {
            TextButton(
                onClick = onCancelButtonClicked
            ) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        } else {
            Box(
                modifier = Modifier
                    .aspectRatio(1F)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable(onClick = onFabClicked),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun Scrim(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32F))
    )
}

@Composable
fun SheetDragHandle(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(
            Modifier.size(
                width = 32.dp,
                height = 4.dp,
            )
        )
    }
}

@Composable
@Preview
private fun AddGroceryBottomSheetContentPreview() {
    var customGrocery by remember { mutableStateOf<Grocery?>(null) }
    AddGroceryBottomSheetContent(
        state = rememberAddGroceryBottomSheetContentState(),
        searchBarFocusRequester = FocusRequester(),
        onSearchInputChanged = { searchInput ->
            if (searchInput.isNotEmpty()) {
                customGrocery = Grocery(
                    id = 1,
                    name = searchInput,
                    purchased = true,
                )
            }
        },
        onGrocerySearchResultClick = {},
        grocerySearchResults = listOf(
            GroceryGroup(
                titleId = null,
                groceries = customGrocery?.let { listOf(it) } ?: emptyList(),
            )
        ),
        onKeyboardDone = {},
    )
}
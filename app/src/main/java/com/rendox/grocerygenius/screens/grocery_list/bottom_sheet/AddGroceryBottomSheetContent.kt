package com.rendox.grocerygenius.screens.grocery_list.bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
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


@Composable
fun AddGroceryBottomSheetContent(
    modifier: Modifier = Modifier,
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
                onSearchInputChanged = state::updateSearchInput,
                onFocusChanged = state::onSearchBarFocusChanged,
                useExpandedPlaceholderText = state.useExpandedPlaceholderText,
                clearSearchInputButtonIsShown = state.clearSearchInputButtonIsShown,
                onClearSearchInputClicked = state::clearSearchInput,
                onKeyboardDone = state::onKeyboardDone,
            )
            Fab(
                showCancelButtonInsteadOfFab = state.showCancelButtonInsteadOfFab,
                onCancelButtonClicked = state::cancelButtonOnClick,
                onFabClicked = state::fabOnClick,
            )
        }
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
@Preview
private fun AddGroceryBottomSheetContentPreview() {
    AddGroceryBottomSheetContent(
        state = rememberAddGroceryBottomSheetContentState(),
        searchBarFocusRequester = FocusRequester(),
    )
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
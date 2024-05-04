package com.rendox.grocerygenius.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.ui.theme.CornerRoundingDefault
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    placeholder: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    clearSearchInputButtonIsShown: Boolean,
    onClearSearchInputClicked: () -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var searchQueryTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = searchQuery, selection = when {
                    searchQuery.isEmpty() -> TextRange.Zero
                    else -> TextRange(searchQuery.length, searchQuery.length)
                }
            )
        )
    }
    val textFieldValue = searchQueryTextFieldValue.copy(text = searchQuery)
    SideEffect {
        if (textFieldValue.selection != searchQueryTextFieldValue.selection ||
            textFieldValue.composition != searchQueryTextFieldValue.composition
        ) {
            searchQueryTextFieldValue = textFieldValue
        }
    }

    var lastTextValue by remember(searchQuery) { mutableStateOf(searchQuery) }
    SearchField(
        modifier = modifier,
        searchQuery = textFieldValue,
        onSearchQueryChanged = { newSearchQueryTextFieldValue ->
            searchQueryTextFieldValue = newSearchQueryTextFieldValue
            val stringChangedSinceLastInvocation =
                lastTextValue != newSearchQueryTextFieldValue.text
            lastTextValue = newSearchQueryTextFieldValue.text
            if (stringChangedSinceLastInvocation) {
                onSearchQueryChanged(newSearchQueryTextFieldValue.text)
            }
        },
        placeholder = placeholder,
        clearSearchInputButtonIsShown = clearSearchInputButtonIsShown,
        onClearSearchInputClicked = onClearSearchInputClicked,
        keyboardActions = keyboardActions,
        leadingIcon = leadingIcon,
    )
}

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    searchQuery: TextFieldValue,
    onSearchQueryChanged: (TextFieldValue) -> Unit,
    placeholder: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    clearSearchInputButtonIsShown: Boolean,
    onClearSearchInputClicked: () -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    TextField(
        modifier = modifier,
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        placeholder = placeholder,
        shape = CornerRoundingDefault,
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
        keyboardActions = keyboardActions,
        trailingIcon = {
            val contentDescription =
                stringResource(R.string.add_grocery_search_field_trailing_icon_description)
            if (clearSearchInputButtonIsShown) {
                IconButton(onClick = onClearSearchInputClicked) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = contentDescription,
                    )
                }
            }
        },
        leadingIcon = leadingIcon,
    )
}

@Preview
@Composable
private fun SearchFieldPreview() {
    GroceryGeniusTheme {
        Surface {
            SearchField(
                searchQuery = TextFieldValue(""),
                onSearchQueryChanged = {},
                clearSearchInputButtonIsShown = true,
                onClearSearchInputClicked = {},
                placeholder = { Text("Search") },
            )
        }
    }
}
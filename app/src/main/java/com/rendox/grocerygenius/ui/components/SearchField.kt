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
import androidx.compose.runtime.remember
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
    clearSearchInputButtonIsShown: Boolean,
    onClearSearchInputClicked: () -> Unit,
    onKeyboardDone: () -> Unit,
) {
    val searchQueryTextFieldValue = remember(searchQuery) {
        TextFieldValue(
            text = searchQuery,
            selection = TextRange(searchQuery.length),
        )
    }
    SearchField(
        modifier = modifier,
        searchQuery = searchQueryTextFieldValue,
        onSearchQueryChanged = { onSearchQueryChanged(it.text) },
        placeholder = placeholder,
        clearSearchInputButtonIsShown = clearSearchInputButtonIsShown,
        onClearSearchInputClicked = onClearSearchInputClicked,
        onKeyboardDone = onKeyboardDone,
    )
}

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    searchQuery: TextFieldValue,
    onSearchQueryChanged: (TextFieldValue) -> Unit,
    placeholder: @Composable () -> Unit,
    clearSearchInputButtonIsShown: Boolean,
    onClearSearchInputClicked: () -> Unit,
    onKeyboardDone: () -> Unit,
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
        keyboardActions = KeyboardActions(
            onDone = { onKeyboardDone() }
        ),
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
        }
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
                onKeyboardDone = {},
                placeholder = { Text("Search") },
            )
        }
    }
}
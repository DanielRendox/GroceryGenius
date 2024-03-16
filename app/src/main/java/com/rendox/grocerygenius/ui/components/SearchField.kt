package com.rendox.grocerygenius.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    searchInput: String,
    onSearchInputChanged: (String) -> Unit,
    placeholder: @Composable () -> Unit,
    clearSearchInputButtonIsShown: Boolean,
    onClearSearchInputClicked: () -> Unit,
    onKeyboardDone: () -> Unit,
) {
    TextField(
        modifier = modifier,
        value = searchInput,
        onValueChange = onSearchInputChanged,
        placeholder = placeholder,
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
                searchInput = "",
                onSearchInputChanged = {},
                clearSearchInputButtonIsShown = true,
                onClearSearchInputClicked = {},
                onKeyboardDone = {},
                placeholder = { Text("Search") },
            )
        }
    }
}
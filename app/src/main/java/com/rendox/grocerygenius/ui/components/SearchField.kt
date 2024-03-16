package com.rendox.grocerygenius.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState = rememberTextFieldState(),
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    clearSearchInputButtonIsShown: Boolean,
    onClearSearchInputClicked: () -> Unit,
    onKeyboardDone: () -> Unit,
) {
    BasicTextField2(
        modifier = modifier,
        state = textFieldState,
        textStyle = MaterialTheme.typography.bodyLarge,
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = { onKeyboardDone() }
        ),
        decorator = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = textFieldState.text.toString(),
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder = placeholder,
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
                shape = RoundedCornerShape(20),
                singleLine = true,
                enabled = true,
                isError = isError,
                interactionSource = remember { MutableInteractionSource() },
                colors = TextFieldDefaults.colors().copy(
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun SearchFieldPreview() {
    GroceryGeniusTheme {
        Surface {
            SearchField(
                textFieldState = rememberTextFieldState(),
                clearSearchInputButtonIsShown = true,
                onClearSearchInputClicked = {},
                onKeyboardDone = {},
            )
        }
    }
}
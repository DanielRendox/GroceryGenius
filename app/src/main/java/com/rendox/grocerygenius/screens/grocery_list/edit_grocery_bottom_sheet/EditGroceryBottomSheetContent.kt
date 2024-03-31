package com.rendox.grocerygenius.screens.grocery_list.edit_grocery_bottom_sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.ui.components.SearchField
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

@Composable
fun EditGroceryBottomSheetContent(
    modifier: Modifier = Modifier,
    groceryName: String,
    groceryDescription: String?,
    clearGroceryDescriptionButtonIsShown: Boolean,
    onGroceryDescriptionChanged: (String) -> Unit,
    onClearGroceryDescription: () -> Unit,
    onDoneButtonClick: () -> Unit,
    onKeyboardDone: () -> Unit,
    itemDescriptionFocusRequester: FocusRequester,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1F),
                text = groceryName,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            TextButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = onDoneButtonClick,
            ) {
                Text(text = stringResource(R.string.done))
            }
        }

        SearchField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .focusRequester(itemDescriptionFocusRequester),
            searchInput = groceryDescription ?: "",
            onSearchInputChanged = onGroceryDescriptionChanged,
            placeholder = {
                Text(text = stringResource(R.string.add_grocery_item_description_placeholder))
            },
            clearSearchInputButtonIsShown = clearGroceryDescriptionButtonIsShown,
            onClearSearchInputClicked = onClearGroceryDescription,
            onKeyboardDone = onKeyboardDone,
        )
    }
}

@Preview
@Composable
private fun EditGroceryBottomSheetContentPreview() {
    GroceryGeniusTheme {
        Surface {
            EditGroceryBottomSheetContent(
                modifier = Modifier.padding(16.dp),
                groceryName = "Tea",
                groceryDescription = "Green, 32 bags",
                clearGroceryDescriptionButtonIsShown = false,
                onGroceryDescriptionChanged = {},
                onClearGroceryDescription = {},
                onKeyboardDone = {},
                onDoneButtonClick = {},
                itemDescriptionFocusRequester = remember { FocusRequester() },
            )
        }
    }
}
package com.rendox.grocerygenius.screens.grocery_list.edit_grocery_bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.ui.components.SearchField
import com.rendox.grocerygenius.ui.theme.CornerRoundingDefault
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme
import com.rendox.grocerygenius.ui.theme.extendedColors

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
    onChangeCategoryClick: () -> Unit,
    onChangeIconClick: () -> Unit,
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
                Text(text = stringResource(R.string.edit_grocery_item_description_placeholder))
            },
            clearSearchInputButtonIsShown = clearGroceryDescriptionButtonIsShown,
            onClearSearchInputClicked = onClearGroceryDescription,
            onKeyboardDone = onKeyboardDone,
        )

        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = "Settings",
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 16.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SettingButton(
                modifier = Modifier.weight(1F),
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_folder_24),
                        contentDescription = null,
                    )
                },
                title = stringResource(R.string.edit_grocery_change_category_button_title),
                onClick = onChangeCategoryClick,
            )
            SettingButton(
                modifier = Modifier.weight(1F),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                    )
                },
                title = stringResource(R.string.edit_grocery_change_icon_button_title),
                onClick = onChangeIconClick,
            )
        }
        FilledTonalButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {  },
            shape = CornerRoundingDefault,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        ) {
            Text(
                text = stringResource(R.string.edit_grocery_remove_item_button_title),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        FilledTonalButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { },
            shape = CornerRoundingDefault,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.extendedColors.redAccent,
            )
        ) {
            Text(
                text = stringResource(R.string.edit_grocery_delete_grocery_button_title),
                color = MaterialTheme.extendedColors.onRedAccent,
            )
        }
    }
}

@Composable
private fun SettingButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(CornerRoundingDefault)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        icon()
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = title,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@PreviewLightDark
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
                onChangeCategoryClick = {},
                onChangeIconClick = {},
            )
        }
    }
}
package com.rendox.grocerygenius.feature.edit_grocery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.ui.components.SearchField
import com.rendox.grocerygenius.ui.theme.CornerRoundingDefault
import com.rendox.grocerygenius.ui.theme.extendedColors

@Composable
fun EditGroceryBottomSheetContent(
    modifier: Modifier = Modifier,
    groceryName: String,
    groceryDescription: TextFieldValue,
    clearGroceryDescriptionButtonIsShown: Boolean,
    productCanBeModified: Boolean,
    onGroceryDescriptionChanged: (TextFieldValue) -> Unit,
    onClearGroceryDescription: () -> Unit,
    onDoneButtonClick: () -> Unit,
    onKeyboardDone: () -> Unit,
    onChangeCategoryClick: () -> Unit,
    onChangeIconClick: () -> Unit,
    onRemoveGrocery: () -> Unit,
    onDeleteProduct: () -> Unit,
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
            searchQuery = groceryDescription,
            onSearchQueryChanged = onGroceryDescriptionChanged,
            placeholder = {
                Text(text = stringResource(R.string.edit_grocery_item_description_placeholder))
            },
            keyboardActions = KeyboardActions(
                onDone = { onKeyboardDone() }
            ),
            clearSearchInputButtonIsShown = clearGroceryDescriptionButtonIsShown,
            onClearSearchInputClicked = onClearGroceryDescription,
        )

        if (productCanBeModified) {
            GrocerySettings(
                modifier = Modifier.padding(top = 32.dp),
                onChangeCategoryClick = onChangeCategoryClick,
                onChangeIconClick = onChangeIconClick,
            )
        }

        FilledTonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            onClick = onRemoveGrocery,
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

        if (productCanBeModified) {
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDeleteProduct,
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
}

@Composable
private fun GrocerySettings(
    modifier: Modifier = Modifier,
    onChangeCategoryClick: () -> Unit,
    onChangeIconClick: () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.edit_grocery_settings_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            modifier = Modifier.padding(top = 8.dp),
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
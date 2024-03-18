package com.rendox.grocerygenius.screens.grocery_list.edit_grocery_bottom_sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.ui.components.GroceryCompact
import com.rendox.grocerygenius.ui.components.SearchField
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditGroceryBottomSheetContent(
    modifier: Modifier = Modifier,
    groceryName: String,
    groceryDescription: String?,
    chosenCategoryId: Int,
    clearGroceryDescriptionButtonIsShown: Boolean,
    onGroceryDescriptionChanged: (String) -> Unit,
    onClearGroceryDescription: () -> Unit,
    onDoneButtonClick: () -> Unit,
    onKeyboardDone: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    categories: List<Category>,
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
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (category in categories) {
                GroceryCompact(
                    modifier = Modifier.clickable {
                        onCategoryClick(category)
                    },
                    title = category.name,
                    icon = {
                        Icon(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .size(32.dp),
                            painter = painterResource(R.drawable.sample_grocery_icon),
                            contentDescription = null,
                        )
                    },
                    backgroundColor = if (category.id == chosenCategoryId) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun EditGroceryBottomSheetContentPreview() {
    val sampleCategories = remember {
        categoryTitlesSample.mapIndexed { index, name ->
            Category(id = index, name = name, iconUri = "")
        }
    }
    GroceryGeniusTheme {
        Surface {
            EditGroceryBottomSheetContent(
                modifier = Modifier.padding(16.dp),
                groceryName = "Tea",
                groceryDescription = "Green, 32 bags",
                clearGroceryDescriptionButtonIsShown = false,
                chosenCategoryId = 2,
                onGroceryDescriptionChanged = {},
                onClearGroceryDescription = {},
                onKeyboardDone = {},
                categories = sampleCategories,
                onDoneButtonClick = {},
                onCategoryClick = {},
                itemDescriptionFocusRequester = remember { FocusRequester() }
            )
        }
    }
}

val categoryTitlesSample = listOf(
    "Fruits and Vegetables",
    "Bakery",
    "Frozen and Convenience",
    "Dairy",
    "Meat",
    "Seafood",
    "Ingredients and Spices",
    "Pet supplies",
)
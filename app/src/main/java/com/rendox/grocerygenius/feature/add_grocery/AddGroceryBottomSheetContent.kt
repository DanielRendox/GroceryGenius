package com.rendox.grocerygenius.feature.add_grocery

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.model.Product
import com.rendox.grocerygenius.ui.components.SearchField
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGrid
import com.rendox.grocerygenius.ui.components.grocery_list.GroceryGridItem
import com.rendox.grocerygenius.ui.components.grocery_list.groceryListItemColors
import com.rendox.grocerygenius.ui.helpers.ObserveUiEvent
import com.rendox.grocerygenius.ui.helpers.UiEvent
import com.rendox.grocerygenius.ui.theme.CornerRoundingDefault
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme
import java.io.File
import kotlin.random.Random

@Composable
fun AddGroceryBottomSheetContent(
    modifier: Modifier = Modifier,
    searchQuery: String,
    clearSearchQueryButtonIsShown: Boolean,
    contentType: AddGroceryBottomSheetContentType,
    customProducts: List<Product>,
    previousGrocery: Grocery?,
    grocerySearchResults: List<Grocery>,
    showExtendedContent: Boolean,
    useExpandedPlaceholderText: Boolean,
    showCancelButtonInsteadOfFab: Boolean,
    changeSearchFieldFocusEvent: UiEvent<Boolean>?,
    onKeyboardDone: () -> Unit,
    onCancelButtonClicked: () -> Unit,
    onFabClicked: () -> Unit,
    onSearchFieldFocusChanged: (FocusState) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    onGrocerySearchResultClick: (Grocery) -> Unit,
    onCustomProductClick: (Product) -> Unit,
    onEditGroceryClicked: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    ObserveUiEvent(changeSearchFieldFocusEvent) { isFocused ->
        if (isFocused) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    Column(modifier = modifier) {
        BottomSheetHeader(
            searchQuery = searchQuery,
            useExpandedPlaceholderText = useExpandedPlaceholderText,
            clearSearchInputButtonIsShown = clearSearchQueryButtonIsShown,
            showCancelButtonInsteadOfFab = showCancelButtonInsteadOfFab,
            onFocusChanged = onSearchFieldFocusChanged,
            onKeyboardDone = onKeyboardDone,
            onCancelButtonClicked = onCancelButtonClicked,
            onFabClicked = onFabClicked,
            focusRequester = focusRequester,
            onSearchQueryChanged = onSearchQueryChanged,
            clearSearchQuery = onClearSearchQuery,
        )
        if (showExtendedContent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (contentType) {
                    AddGroceryBottomSheetContentType.Suggestions -> {}

                    AddGroceryBottomSheetContentType.SearchResults -> {
                        SearchResults(
                            grocerySearchResults = grocerySearchResults,
                            onGrocerySearchResultClick = onGrocerySearchResultClick,
                            customProducts = customProducts,
                            onCustomProductClick = onCustomProductClick,
                        )
                    }

                    AddGroceryBottomSheetContentType.RefineItemOptions -> {
                        if (previousGrocery != null) {
                            RefineItemOptions(
                                groceryName = previousGrocery.name,
                                onEditGroceryClicked = onEditGroceryClicked,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomSheetHeader(
    searchQuery: String,
    useExpandedPlaceholderText: Boolean,
    clearSearchInputButtonIsShown: Boolean,
    showCancelButtonInsteadOfFab: Boolean,
    focusRequester: FocusRequester,
    onFocusChanged: (FocusState) -> Unit,
    onCancelButtonClicked: () -> Unit,
    onFabClicked: () -> Unit,
    onKeyboardDone: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    clearSearchQuery: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        SearchField(
            modifier = Modifier
                .weight(1F)
                .onFocusChanged(onFocusChanged = onFocusChanged)
                .focusRequester(focusRequester),
            searchQuery = searchQuery,
            placeholder = {
                Text(
                    text = if (useExpandedPlaceholderText) {
                        stringResource(R.string.add_grocery_search_field_placeholder_expanded)
                    } else {
                        stringResource(R.string.add_grocery_search_field_placeholder_collapsed)
                    },
                )
            },
            clearSearchInputButtonIsShown = clearSearchInputButtonIsShown,
            onClearSearchInputClicked = clearSearchQuery,
            keyboardActions = KeyboardActions(
                onDone = { onKeyboardDone() }
            ),
            onSearchQueryChanged = onSearchQueryChanged,
        )
        Fab(
            showCancelButtonInsteadOfFab = showCancelButtonInsteadOfFab,
            onCancelButtonClicked = onCancelButtonClicked,
            onFabClicked = onFabClicked,
        )
    }
}

@Composable
private fun SearchResults(
    modifier: Modifier = Modifier,
    grocerySearchResults: List<Grocery>,
    customProducts: List<Product>,
    onGrocerySearchResultClick: (Grocery) -> Unit,
    onCustomProductClick: (Product) -> Unit,
) {
    val context = LocalContext.current
    LazyGroceryGrid(
        modifier = modifier,
        groceries = grocerySearchResults,
        groceryItem = { grocery ->
            GroceryGridItem(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onGrocerySearchResultClick(grocery) },
                groceryName = grocery.name,
                groceryDescription = grocery.description,
                color = if (grocery.purchased) {
                    MaterialTheme.colorScheme.groceryListItemColors.purchasedBackgroundColor
                } else {
                    MaterialTheme.colorScheme.groceryListItemColors.defaultBackgroundColor
                },
                iconFile = remember(grocery.icon?.filePath) {
                    grocery.icon?.filePath?.let { filePath ->
                        File(context.filesDir, filePath)
                    }
                }
            )
        },
        customProducts = customProducts,
        customProduct = { product ->
            GroceryGridItem(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onCustomProductClick(product) },
                groceryName = product.name,
                groceryDescription = null,
                color = MaterialTheme.colorScheme.groceryListItemColors.purchasedBackgroundColor,
                iconFile = remember(product.icon?.filePath) {
                    product.icon?.filePath?.let { filePath ->
                        File(context.filesDir, filePath)
                    }
                }
            )
        },
        showScrollbar = false,
    )
}

@Composable
private fun RefineItemOptions(
    modifier: Modifier = Modifier,
    groceryName: String,
    onEditGroceryClicked: () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(
                R.string.add_grocery_refine_item_options_title,
                groceryName,
            ),
            style = MaterialTheme.typography.titleMedium,
        )

        Row(
            modifier = Modifier
                .clip(shape = CornerRoundingDefault)
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .clickable(onClick = onEditGroceryClicked)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                imageVector = Icons.Default.Edit,
                contentDescription = null,
            )
            Text(
                text = stringResource(R.string.add_grocery_edit_item_button_title),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
private fun SearchResultsPreview() {
    val searchResults = remember {
        List(8) { index ->
            Grocery(
                productId = index.toString(),
                name = "Grocery $index",
                purchased = Random.nextBoolean(),
                description = "Description $index",
                category = Category(
                    id = index.toString(),
                    name = "Category$index",
                    sortingPriority = index.toLong(),
                ),
            )
        }
    }

    GroceryGeniusTheme {
        Surface {
            SearchResults(
                grocerySearchResults = searchResults,
                onGrocerySearchResultClick = {},
                customProducts = emptyList(),
                onCustomProductClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun RefineItemOptionsPreview() {
    GroceryGeniusTheme {
        Surface {
            RefineItemOptions(
                groceryName = "Test grocery",
                onEditGroceryClicked = {},
            )
        }
    }
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
                Text(text = stringResource(id = R.string.done))
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
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

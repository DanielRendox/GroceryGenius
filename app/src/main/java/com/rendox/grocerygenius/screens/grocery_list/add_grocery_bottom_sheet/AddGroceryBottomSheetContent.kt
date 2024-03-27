package com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.CustomProduct
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.ui.components.SearchField
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGrid
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGridItem
import com.rendox.grocerygenius.ui.components.grocery_list.groceryListItemColors
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme
import kotlin.random.Random

@Composable
fun AddGroceryBottomSheetContent(
    modifier: Modifier = Modifier,
    searchInput: String,
    useExpandedPlaceholderText: Boolean,
    clearSearchInputButtonIsShown: Boolean,
    showCancelButtonInsteadOfFab: Boolean,
    grocerySearchResults: List<Grocery>,
    handleBackButtonPress: Boolean,
    contentType: BottomSheetContentType,
    previousGrocery: Grocery?,
    showExtendedContent: Boolean,
    focusRequester: FocusRequester,
    customProduct: CustomProduct? = null,
    onGrocerySearchResultClick: (Grocery) -> Unit,
    onSearchFieldFocusChanged: (FocusState) -> Unit,
    onBackButtonClicked: () -> Unit,
    onKeyboardDone: () -> Unit,
    cancelButtonOnClick: () -> Unit,
    fabOnClick: () -> Unit,
    onSearchInputChanged: (String) -> Unit,
    clearSearchInput: () -> Unit,
    editGroceryOnClick: (Grocery) -> Unit,
    onCustomProductClick: (CustomProduct) -> Unit,
) {
    BackHandler(
        enabled = handleBackButtonPress,
        onBack = onBackButtonClicked,
    )

    Column(modifier = modifier) {
        BottomSheetHeader(
            searchInput = searchInput,
            useExpandedPlaceholderText = useExpandedPlaceholderText,
            clearSearchInputButtonIsShown = clearSearchInputButtonIsShown,
            showCancelButtonInsteadOfFab = showCancelButtonInsteadOfFab,
            onFocusChanged = onSearchFieldFocusChanged,
            onKeyboardDone = onKeyboardDone,
            cancelButtonOnClick = cancelButtonOnClick,
            fabOnClick = fabOnClick,
            focusRequester = focusRequester,
            onSearchInputChanged = onSearchInputChanged,
            clearSearchInput = clearSearchInput,
        )
        if (showExtendedContent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (contentType) {
                    BottomSheetContentType.Suggestions -> {}

                    BottomSheetContentType.SearchResults -> {
                        SearchResults(
                            grocerySearchResults = grocerySearchResults,
                            onGrocerySearchResultClick = onGrocerySearchResultClick,
                            customProduct = customProduct,
                            onCustomProductClick = onCustomProductClick,
                        )
                    }

                    BottomSheetContentType.RefineItemOptions -> {
                        if (previousGrocery != null) {
                            RefineItemOptions(
                                groceryName = previousGrocery.name,
                                editGroceryOnClick = {
                                    editGroceryOnClick(previousGrocery)
                                },
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
    searchInput: String,
    useExpandedPlaceholderText: Boolean,
    clearSearchInputButtonIsShown: Boolean,
    showCancelButtonInsteadOfFab: Boolean,
    focusRequester: FocusRequester,
    onFocusChanged: (FocusState) -> Unit,
    cancelButtonOnClick: () -> Unit,
    fabOnClick: () -> Unit,
    onKeyboardDone: () -> Unit,
    onSearchInputChanged: (String) -> Unit,
    clearSearchInput: () -> Unit,
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
            searchInput = searchInput,
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
            onClearSearchInputClicked = clearSearchInput,
            onKeyboardDone = onKeyboardDone,
            onSearchInputChanged = onSearchInputChanged,
        )
        Fab(
            showCancelButtonInsteadOfFab = showCancelButtonInsteadOfFab,
            onCancelButtonClicked = cancelButtonOnClick,
            onFabClicked = fabOnClick,
        )
    }
}

@Composable
private fun SearchResults(
    modifier: Modifier = Modifier,
    grocerySearchResults: List<Grocery>,
    customProduct: CustomProduct?,
    onGrocerySearchResultClick: (Grocery) -> Unit,
    onCustomProductClick: (CustomProduct) -> Unit,
) {
    LazyGroceryGrid(
        modifier = modifier,
        groceries = grocerySearchResults,
        groceryItem = { grocery ->
            LazyGroceryGridItem(
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
                groceryIcon = grocery.icon,
            )
        },
        customProduct = customProduct?.let { product ->
            {
                LazyGroceryGridItem(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onCustomProductClick(product) },
                    groceryName = product.name,
                    groceryDescription = product.description,
                    color = MaterialTheme.colorScheme.groceryListItemColors.defaultBackgroundColor,
                    groceryIcon = null,
                )
            }
        }
    )
}

@Composable
private fun RefineItemOptions(
    modifier: Modifier = Modifier,
    groceryName: String,
    editGroceryOnClick: () -> Unit,
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
                .clip(shape = RoundedCornerShape(20))
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .clickable(onClick = editGroceryOnClick)
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
                productId = index,
                name = "Grocery $index",
                purchased = Random.nextBoolean(),
                description = "Description $index",
                category = Category(
                    id = index,
                    name = "Category$index",
                    sortingPriority = index,
                    isDefault = false,
                ),
            )
        }
    }

    GroceryGeniusTheme {
        Surface {
            SearchResults(
                grocerySearchResults = searchResults,
                onGrocerySearchResultClick = {},
                customProduct = null,
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
                editGroceryOnClick = {},
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
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

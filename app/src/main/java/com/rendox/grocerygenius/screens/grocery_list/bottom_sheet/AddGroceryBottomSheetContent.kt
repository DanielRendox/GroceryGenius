package com.rendox.grocerygenius.screens.grocery_list.bottom_sheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.ui.components.SearchField
import com.rendox.grocerygenius.ui.components.grocery_list.GroceryGroup
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGrid
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGridItem
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme
import kotlin.random.Random


@Composable
fun AddGroceryBottomSheetContent(
    modifier: Modifier = Modifier,
    onSearchInputChanged: (String) -> Unit,
    onGrocerySearchResultClick: (Grocery) -> Unit,
    grocerySearchResults: List<GroceryGroup>,
    onKeyboardDone: () -> Unit,
    state: AddGroceryBottomSheetContentState = rememberAddGroceryBottomSheetContentState(),
    searchBarFocusRequester: FocusRequester,
) {
    Column(modifier = modifier) {
        BottomSheetHeader(
            searchInput = state.searchInput,
            useExpandedPlaceholderText = state.useExpandedPlaceholderText,
            clearSearchInputButtonIsShown = state.clearSearchInputButtonIsShown,
            showCancelButtonInsteadOfFab = state.showCancelButtonInsteadOfFab,
            onFocusChanged = state::onSearchBarFocusChanged,
            updateSearchInput = { searchInput ->
                state.updateSearchInput(searchInput)
                onSearchInputChanged(searchInput)
            },
            clearSearchInput = state::clearSearchInput,
            cancelButtonOnClick = state::cancelButtonOnClick,
            fabOnClick = state::fabOnClick,
            searchBarFocusRequester = searchBarFocusRequester,
            onSearchInputChanged = onSearchInputChanged,
            onKeyboardDone = {
                onKeyboardDone()
                state.onKeyboardDone()
            },
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (state.contentType) {
                BottomSheetContentType.HeaderOnly -> {}
                BottomSheetContentType.Suggestions -> {}

                BottomSheetContentType.SearchResults -> {
                    SearchResults(
                        grocerySearchResults = grocerySearchResults,
                        onGrocerySearchResultClick = { grocery ->
                            onGrocerySearchResultClick(grocery)
                            state.onGroceryItemClick(grocery)
                        },
                    )
                }

                BottomSheetContentType.RefineItemOptions -> {
                    RefineItemOptions(
                        groceryName = state.previousGroceryName ?: "",
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BottomSheetHeader(
    searchInput: String,
    useExpandedPlaceholderText: Boolean,
    clearSearchInputButtonIsShown: Boolean,
    showCancelButtonInsteadOfFab: Boolean,
    onFocusChanged: (FocusState) -> Unit,
    updateSearchInput: (String) -> Unit,
    clearSearchInput: () -> Unit,
    cancelButtonOnClick: () -> Unit,
    fabOnClick: () -> Unit,
    searchBarFocusRequester: FocusRequester,
    onSearchInputChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        val searchFieldState = rememberTextFieldState()
        SearchField(
            modifier = Modifier
                .weight(1F)
                .onFocusChanged(onFocusChanged = onFocusChanged)
                .focusRequester(focusRequester = searchBarFocusRequester),
    //            onSearchInputChanged = { searchInput ->
    //                updateSearchInput(searchInput)
    //                onSearchInputChanged(searchInput)
    //            },
            textFieldState = searchFieldState,
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
            onKeyboardDone = {
                onKeyboardDone()
            },
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
    grocerySearchResults: List<GroceryGroup>,
    onGrocerySearchResultClick: (Grocery) -> Unit,
) {
    LazyGroceryGrid(
        modifier = modifier,
        groceryGroups = grocerySearchResults,
        groceryItem = { grocery ->
            LazyGroceryGridItem(
                modifier = Modifier.fillMaxSize(),
                grocery = grocery,
                onClick = { onGrocerySearchResultClick(grocery) },
            )
        },
    )
}

@Composable
private fun RefineItemOptions(
    modifier: Modifier = Modifier,
    groceryName: String,
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
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                imageVector = Icons.Default.Edit,
                contentDescription = null,
            )
            Text(
                text = "Enter item details",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
private fun SearchResultsPreview() {
    val searchResults = remember {
        listOf(
            GroceryGroup(
                titleId = null,
                groceries = List(8) { index ->
                    Grocery(
                        id = index,
                        name = "Grocery $index",
                        purchased = Random.nextBoolean(),
                    )
                }
            )
        )
    }

    GroceryGeniusTheme {
        Surface {
            SearchResults(
                grocerySearchResults = searchResults,
                onGrocerySearchResultClick = {},
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
            )
        }
    }
}

@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
    searchInput: String,
    onSearchInputChanged: (String) -> Unit,
    placeHolderText: String,
    clearSearchInputButtonIsShown: Boolean,
    onClearSearchInputClicked: () -> Unit,
    onKeyboardDone: () -> Unit,
) {
    TextField(
        modifier = modifier,
        value = searchInput,
        onValueChange = onSearchInputChanged,
        placeholder = {
            Text(text = placeHolderText)
        },
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
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onFabClicked),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Composable
fun SheetDragHandle(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(
            Modifier.size(
                width = 32.dp,
                height = 4.dp,
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditGrocery(
    modifier: Modifier = Modifier,
    groceryName: String,
    groceryDescription: String?,
    updateGroceryDescription: (String) -> Unit,
    clearGroceryDescription: () -> Unit,
    onKeyboardDone: () -> Unit,
    categories: List<String>
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            text = stringResource(
                R.string.add_grocery_refine_item_options_title,
                groceryName,
            ),
            style = MaterialTheme.typography.headlineSmall,
        )
        SearchField(
            modifier = Modifier.fillMaxWidth(),
            searchInput = groceryDescription ?: "",
            onSearchInputChanged = updateGroceryDescription,
            placeHolderText = stringResource(R.string.add_grocery_item_description_placeholder),
            clearSearchInputButtonIsShown = groceryDescription?.isNotEmpty() ?: false,
            onClearSearchInputClicked = clearGroceryDescription,
            onKeyboardDone = onKeyboardDone,
        )
        FlowRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            var selectedCategory by remember { mutableStateOf<String?>(null) }
            for (title in categories) {
                val selected = title == selectedCategory
                Category(
                    modifier = Modifier.clickable {
                        selectedCategory =
                            if (selectedCategory == title) null else title
                    },
                    title = title,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.apple),
                            contentDescription = null,
                        )
                    },
                    backgroundColor = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                    },
                )
            }
            Category(
                title = stringResource(R.string.add_grocery_add_new_category_chip_title),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_grocery_add_new_category_chip_description),
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
            )
        }
    }
}

@Composable
private fun Category(
    modifier: Modifier = Modifier,
    title: String,
    backgroundColor: Color = Color.Unspecified,
    icon: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20),
        color = backgroundColor,
    ) {
        Row(
            modifier = modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .height(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

//@Composable
//@Preview
//@PreviewLightDark
//private fun AddGroceryBottomSheetContentPreview() {
//    var customGrocery by remember { mutableStateOf<Grocery?>(null) }
//    GroceryGeniusTheme {
//        Surface(modifier = Modifier.fillMaxSize()) {
//            AddGroceryBottomSheetContent(
//                state = rememberAddGroceryBottomSheetContentState(
//                    contentType = BottomSheetContentType.RefineItemOptions,
//                    previousGroceryName = "Test grocery",
//                    previousGroceryId = 1,
//                ),
//                searchBarFocusRequester = FocusRequester(),
//                onSearchInputChanged = { searchInput ->
//                    if (searchInput.isNotEmpty()) {
//                        customGrocery = Grocery(
//                            id = 1,
//                            name = searchInput,
//                            purchased = true,
//                        )
//                    }
//                },
//                onGrocerySearchResultClick = {},
//                grocerySearchResults = listOf(
//                    GroceryGroup(
//                        titleId = null,
//                        groceries = customGrocery?.let { listOf(it) } ?: emptyList(),
//                    )
//                ),
//                onKeyboardDone = {},
//            )
//        }
//    }
//}

//@OptIn(ExperimentalLayoutApi::class)
//@Preview(widthDp = 360, heightDp = 640, showBackground = true)
//@Composable
//private fun CategoryPreview() {
//    FlowRow(
//        horizontalArrangement = Arrangement.spacedBy(8.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//    ) {
//        for (title in categoryTitlesSample) {
//            Category(
//                title = title,
//                icon = {
//                    Icon(
//                        imageVector = Icons.Default.Build,
//                        contentDescription = null,
//                    )
//                }
//            )
//        }
//    }
//}

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
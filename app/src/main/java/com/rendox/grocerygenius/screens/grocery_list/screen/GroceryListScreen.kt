package com.rendox.grocerygenius.screens.grocery_list.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet.AddGroceryBottomSheetContent
import com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet.BottomSheetContentType
import com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet.rememberAddGroceryBottomSheetState
import com.rendox.grocerygenius.screens.grocery_list.edit_grocery_bottom_sheet.EditGroceryBottomSheetContent
import com.rendox.grocerygenius.ui.components.BottomSheetDragHandle
import com.rendox.grocerygenius.ui.components.Scrim
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbar
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbarScaffoldScrollableState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.CollapsingToolbarNestedScrollConnection
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.ToolbarState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.rememberExitUntilCollapsedToolbarState
import com.rendox.grocerygenius.ui.components.grocery_list.GroceryGroup
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGrid
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGridItem
import com.rendox.grocerygenius.ui.helpers.ObserveUiEvent
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun GroceryListRoute(
    modifier: Modifier = Modifier,
    viewModel: GroceryListScreenViewModel = viewModel(),
) {
    val groceries by viewModel.groceriesFlow.collectAsState()
    val grocerySearchResults by viewModel.grocerySearchResultsFlow.collectAsState()
    val clearSearchInputButtonIsShown by viewModel.clearSearchInputButtonIsShownFlow.collectAsState()
    val bottomSheetContentType by viewModel.bottomSheetContentTypeFlow.collectAsState()
    val previousGroceryName by viewModel.previousGroceryFlow.collectAsState()
    val editGrocery by viewModel.editGroceryFlow.collectAsState()
    val clearEditGroceryDescriptionButtonIsShown by viewModel.clearEditGroceryDescriptionButtonIsShown.collectAsState()

    GroceryListScreen(
        modifier = modifier,
        listName = "Grocery List",
        groceries = groceries,
        searchInput = viewModel.searchInput ?: "",
        grocerySearchResults = grocerySearchResults,
        clearSearchInputButtonIsShown = clearSearchInputButtonIsShown,
        bottomSheetContentType = bottomSheetContentType,
        previousGrocery = previousGroceryName,
        editGrocery = editGrocery,
        clearEditGroceryDescriptionButtonIsShown = clearEditGroceryDescriptionButtonIsShown,
        categories = viewModel.groceryCategories,
        editGroceryDescription = viewModel.editGroceryDescription,
        onGroceryItemClick = viewModel::toggleItemPurchased,
        updateSearchInput = viewModel::updateSearchInput,
        onGrocerySearchResultClick = viewModel::onGrocerySearchResultClick,
        onSearchInputKeyboardDone = viewModel::onSearchInputKeyboardDone,
        clearSearchInput = viewModel::onClearSearchInput,
        onBottomSheetCollapsing = viewModel::onBottomSheetCollapsing,
        updateEditGroceryDescription = viewModel::updateEditGroceryDescription,
        onCategoryClick = viewModel::onEditGroceryCategoryClick,
        onClearGroceryDescription = viewModel::onClearEditGroceryDescription,
        onEditGrocery = viewModel::onEditGrocery,
        onEditGroceryBottomSheetHidden = viewModel::onEditGroceryBottomSheetHidden,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun GroceryListScreen(
    modifier: Modifier = Modifier,
    listName: String,
    searchInput: String,
    groceries: List<GroceryGroup>,
    grocerySearchResults: List<GroceryGroup>,
    clearSearchInputButtonIsShown: Boolean,
    bottomSheetContentType: BottomSheetContentType,
    previousGrocery: Grocery?,
    editGrocery: Grocery?,
    clearEditGroceryDescriptionButtonIsShown: Boolean,
    categories: List<Category>,
    editGroceryDescription: String?,
    onGroceryItemClick: (Grocery) -> Unit,
    updateSearchInput: (String) -> Unit,
    onGrocerySearchResultClick: (Grocery) -> Unit,
    onSearchInputKeyboardDone: () -> Unit,
    clearSearchInput: () -> Unit,
    onBottomSheetCollapsing: () -> Unit,
    onEditGrocery: (Grocery) -> Unit,
    updateEditGroceryDescription: (String) -> Unit,
    onClearGroceryDescription: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    onEditGroceryBottomSheetHidden: () -> Unit,
) {
    val collapsedToolbarHeight = 64.dp
    val expandedToolbarHeight = 112.dp
    val toolbarHeightRange = with(LocalDensity.current) {
        collapsedToolbarHeight.roundToPx()..expandedToolbarHeight.roundToPx()
    }
    val toolbarState = rememberExitUntilCollapsedToolbarState(toolbarHeightRange)
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    val scrollState: CollapsingToolbarScaffoldScrollableState = remember {
        object : CollapsingToolbarScaffoldScrollableState, ScrollableState by lazyGridState {
            override val firstVisibleItemIndex: Int
                get() = lazyGridState.firstVisibleItemIndex
            override val firstVisibleItemScrollOffset: Int
                get() = lazyGridState.firstVisibleItemScrollOffset
        }
    }

    val nestedScrollConnection = remember {
        CollapsingToolbarNestedScrollConnection(
            toolbarState = toolbarState,
            scrollState = scrollState,
            coroutineScope = coroutineScope,
        )
    }

    val navigationBarHeight =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val addBottomSheetState = rememberStandardBottomSheetState()
    val scaffoldState = rememberBottomSheetScaffoldState(addBottomSheetState)
    val addGroceryBottomSheetState =
        rememberAddGroceryBottomSheetState(addBottomSheetState, searchInput)

    LaunchedEffect(addGroceryBottomSheetState.sheetIsCollapsing) {
        if (addGroceryBottomSheetState.sheetIsCollapsing) {
            onBottomSheetCollapsing()
        }
    }

    val focusManager = LocalFocusManager.current
    val searchBarFocusRequester = remember { FocusRequester() }
    val itemDescriptionFocusRequester = remember { FocusRequester() }

    ObserveUiEvent(
        addGroceryBottomSheetState.changeSearchFieldFocusEvent
    ) { isFocused ->
        if (isFocused) {
            searchBarFocusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    val editBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var editGroceryBottomSheetIsVisible by rememberSaveable { mutableStateOf(false) }
    val hideBottomSheet: () -> Unit = remember {
        {
            coroutineScope
                .launch { editBottomSheetState.hide() }
                .invokeOnCompletion {
                    if (!editBottomSheetState.isVisible) {
                        editGroceryBottomSheetIsVisible = false
                    }
                }
        }
    }

    LaunchedEffect(editBottomSheetState.isVisible) {
        if (editBottomSheetState.isVisible) {
            itemDescriptionFocusRequester.requestFocus()
        } else {
            onEditGroceryBottomSheetHidden()
        }
    }

    if (editGroceryBottomSheetIsVisible) {
        ModalBottomSheet(
            modifier = Modifier.padding(top = 60.dp),
            onDismissRequest = hideBottomSheet,
            sheetState = editBottomSheetState,
            scrimColor = Color.Transparent,
            dragHandle = { BottomSheetDragHandle() }
        ) {
            EditGroceryBottomSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight(),
                groceryName = editGrocery?.name ?: "",
                groceryDescription = editGroceryDescription ?: "",
                chosenCategoryId = editGrocery?.category?.id ?: -1,
                clearGroceryDescriptionButtonIsShown = clearEditGroceryDescriptionButtonIsShown,
                onGroceryDescriptionChanged = updateEditGroceryDescription,
                onClearGroceryDescription = onClearGroceryDescription,
                onDoneButtonClick = hideBottomSheet,
                onKeyboardDone = hideBottomSheet,
                onCategoryClick = onCategoryClick,
                categories = categories,
                itemDescriptionFocusRequester = itemDescriptionFocusRequester,
            )
        }
    }

    BottomSheetScaffold(
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
        sheetContent = {
            AddGroceryBottomSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp * 0.75F.dp),
                searchInput = searchInput,
                onSearchInputChanged = updateSearchInput,
                useExpandedPlaceholderText = addGroceryBottomSheetState.useExpandedPlaceHolderText,
                clearSearchInputButtonIsShown = clearSearchInputButtonIsShown,
                showCancelButtonInsteadOfFab = addGroceryBottomSheetState.showCancelButtonInsteadOfFab,
                grocerySearchResults = grocerySearchResults,
                handleBackButtonPress = addGroceryBottomSheetState.handleBackButtonPress,
                onGrocerySearchResultClick = onGrocerySearchResultClick,
                previousGrocery = previousGrocery,
                onSearchFieldFocusChanged = addGroceryBottomSheetState::onSearchFieldFocusChanged,
                onBackButtonClicked = addGroceryBottomSheetState::onSheetDismissed,
                onKeyboardDone = {
                    addGroceryBottomSheetState.onKeyboardDone()
                    onSearchInputKeyboardDone()
                },
                cancelButtonOnClick = addGroceryBottomSheetState::onCancelButtonClicked,
                fabOnClick = addGroceryBottomSheetState::onFabClicked,
                clearSearchInput = clearSearchInput,
                contentType = bottomSheetContentType,
                showExtendedContent = addGroceryBottomSheetState.showExtendedContent,
                editGroceryOnClick = {
                    onEditGrocery(it)
                    editGroceryBottomSheetIsVisible = true
                },
                focusRequester = searchBarFocusRequester,
            )
        },
        sheetDragHandle = { BottomSheetDragHandle() },
        sheetPeekHeight = 100.dp + navigationBarHeight,
        scaffoldState = scaffoldState,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(nestedScrollConnection)
        ) {
            val toolbarIsHidden =
                addBottomSheetState.targetValue == SheetValue.Expanded ||
                        editBottomSheetState.targetValue == SheetValue.Expanded
            AnimatedVisibility(visible = !toolbarIsHidden){
                GroceryListCollapsingToolbar(
                    listName = listName,
                    toolbarHeightRange = toolbarHeightRange,
                    toolbarState = toolbarState,
                )
            }
            LazyGroceryGrid(
                modifier = Modifier
                    .then(
                        if (toolbarIsHidden) Modifier.statusBarsPadding()
                        else Modifier
                    )
                    .fillMaxSize()
                    // stop scroll when the user taps on screen
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                coroutineScope.coroutineContext.cancelChildren()
                            }
                        )
                    },
                lazyGridState = lazyGridState,
                groceryGroups = groceries,
                groceryItem = { grocery ->
                    LazyGroceryGridItem(
                        modifier = Modifier
                            .fillMaxSize()
                            .combinedClickable(
                                onClick = { onGroceryItemClick(grocery) },
                                onLongClick = {
                                    editGroceryBottomSheetIsVisible = true
                                    onEditGrocery(grocery)
                                }
                            ),
                        grocery = grocery,
                    )
                },
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                ),
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        if (
            addBottomSheetState.targetValue == SheetValue.Expanded ||
            editBottomSheetState.targetValue == SheetValue.Expanded
        ) {
            Scrim(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = addGroceryBottomSheetState::onSheetDismissed,
                )
            )
        }
    }
}

@Composable
private fun GroceryListCollapsingToolbar(
    modifier: Modifier = Modifier,
    listName: String,
    toolbarHeightRange: IntRange,
    toolbarState: ToolbarState,
) {
    val expandedTitleStyle = MaterialTheme.typography.headlineSmall
    CollapsingToolbar(
        modifier = modifier.graphicsLayer {
            translationY = toolbarState.offset
        },
        titleExpanded = {
            Text(
                text = listName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = expandedTitleStyle.copy(
                    textMotion = TextMotion.Animated
                ),
            )
        },
        expandedTitleFontSize = expandedTitleStyle.fontSize,
        titleBottomPadding = 24.dp,
        toolbarState = toolbarState,
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        toolbarHeightRange = toolbarHeightRange,
        actions = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                )
            }
        }
    )
}

@Preview
@Composable
fun GroceryListScreenPreview() {
    val dummyGroceries = remember {
        listOf(
            GroceryGroup(
                titleId = null,
                groceries = List(21) { index ->
                    Grocery(
                        id = index,
                        name = "Grocery $index",
                        purchased = Random.nextBoolean(),
                        description = "Description $index",
                        iconUri = "",
                        category = Category(
                            id = 1,
                            name = "Sample",
                            iconUri = "",
                        ),
                    )
                }
            )
        )
    }

    val dummyCategories = remember {
        List(5) { index ->
            Category(
                id = index,
                name = "Category $index",
                iconUri = "",
            )
        }
    }

    GroceryGeniusTheme {
        GroceryListScreen(
            listName = "Grocery List",
            searchInput = "",
            groceries = dummyGroceries,
            grocerySearchResults = dummyGroceries,
            clearSearchInputButtonIsShown = false,
            bottomSheetContentType = BottomSheetContentType.Suggestions,
            previousGrocery = null,
            editGrocery = null,
            clearEditGroceryDescriptionButtonIsShown = false,
            categories = dummyCategories,
            editGroceryDescription = null,
            onGroceryItemClick = {},
            updateSearchInput = {},
            onGrocerySearchResultClick = {},
            onSearchInputKeyboardDone = {},
            clearSearchInput = {},
            onBottomSheetCollapsing = {},
            onEditGrocery = {},
            updateEditGroceryDescription = {},
            onClearGroceryDescription = {},
            onCategoryClick = {},
            onEditGroceryBottomSheetHidden = {},
        )
    }
}
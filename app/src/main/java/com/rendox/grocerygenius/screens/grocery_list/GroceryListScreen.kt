package com.rendox.grocerygenius.screens.grocery_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.CustomProduct
import com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet.AddGroceryBottomSheetContent
import com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet.BottomSheetContentType
import com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet.rememberAddGroceryBottomSheetState
import com.rendox.grocerygenius.screens.grocery_list.edit_grocery_bottom_sheet.EditGroceryBottomSheetContent
import com.rendox.grocerygenius.ui.GroceryPresentation
import com.rendox.grocerygenius.ui.components.BottomSheetDragHandle
import com.rendox.grocerygenius.ui.components.Scrim
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbar
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbarScaffoldScrollableState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.CollapsingToolbarNestedScrollConnection
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.ToolbarState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.rememberExitUntilCollapsedToolbarState
import com.rendox.grocerygenius.ui.components.grocery_list.GroceryGroup
import com.rendox.grocerygenius.ui.components.grocery_list.GroupedLazyGroceryGrid
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGridItem
import com.rendox.grocerygenius.ui.components.grocery_list.groceryListItemColors
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
    val groceryList by viewModel.groceryListFlow.collectAsStateWithLifecycle()
    val groceries by viewModel.groceriesFlow.collectAsStateWithLifecycle()
    val grocerySearchResults by viewModel.grocerySearchResultsFlow.collectAsStateWithLifecycle()
    val clearSearchInputButtonIsShown by viewModel.clearSearchInputButtonIsShownFlow.collectAsStateWithLifecycle()
    val bottomSheetContentType by viewModel.bottomSheetContentTypeFlow.collectAsStateWithLifecycle()
    val previousGrocery by viewModel.previousGroceryFlow.collectAsStateWithLifecycle()
    val editGrocery by viewModel.editGroceryFlow.collectAsStateWithLifecycle()
    val clearEditGroceryDescriptionButtonIsShown by viewModel.clearEditGroceryDescriptionButtonIsShown.collectAsStateWithLifecycle()
    val customProduct by viewModel.customProduct.collectAsStateWithLifecycle()

    GroceryListScreen(
        modifier = modifier,
        listName = groceryList?.name ?: "",
        groceries = groceries,
        searchInput = viewModel.searchInput ?: "",
        grocerySearchResults = grocerySearchResults,
        clearSearchInputButtonIsShown = clearSearchInputButtonIsShown,
        bottomSheetContentType = bottomSheetContentType,
        previousGrocery = previousGrocery,
        editGrocery = editGrocery,
        clearEditGroceryDescriptionButtonIsShown = clearEditGroceryDescriptionButtonIsShown,
        editGroceryDescription = viewModel.editGroceryDescription,
        customProduct = customProduct,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun GroceryListScreen(
    modifier: Modifier = Modifier,
    listName: String,
    searchInput: String,
    groceries: List<GroceryGroup>,
    grocerySearchResults: List<GroceryPresentation>,
    clearSearchInputButtonIsShown: Boolean,
    bottomSheetContentType: BottomSheetContentType,
    previousGrocery: GroceryPresentation?,
    editGrocery: GroceryPresentation?,
    clearEditGroceryDescriptionButtonIsShown: Boolean,
    editGroceryDescription: String?,
    customProduct: CustomProduct?,
    onIntent: (GroceryListScreenIntent) -> Unit,
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
            onIntent(GroceryListScreenIntent.OnAddGroceryBottomSheetCollapsing)
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

    if (editGroceryBottomSheetIsVisible) {
        ModalBottomSheet(
            modifier = Modifier.padding(top = 60.dp),
            onDismissRequest = hideBottomSheet,
            sheetState = editBottomSheetState,
            scrimColor = Color.Transparent,
            dragHandle = { BottomSheetDragHandle() }
        ) {
            LaunchedEffect(editBottomSheetState.currentValue) {
                if (editBottomSheetState.currentValue == SheetValue.Expanded) {
                    itemDescriptionFocusRequester.requestFocus()
                }
            }
            if (editGrocery != null) {
                EditGroceryBottomSheetContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight(),
                    groceryName = editGrocery.name,
                    groceryDescription = editGroceryDescription,
                    clearGroceryDescriptionButtonIsShown = clearEditGroceryDescriptionButtonIsShown,
                    onGroceryDescriptionChanged = {
                        println("onGroceryDescriptionChanged")
                        onIntent(GroceryListScreenIntent.UpdateGroceryDescription(it))
                    },
                    onClearGroceryDescription = {
                        onIntent(GroceryListScreenIntent.OnClearGroceryDescription)
                    },
                    onDoneButtonClick = hideBottomSheet,
                    onKeyboardDone = hideBottomSheet,
                    itemDescriptionFocusRequester = itemDescriptionFocusRequester,
                )
            }
        }
    }

    BottomSheetScaffold(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
            .consumeWindowInsets(WindowInsets.navigationBars),
        sheetContent = {
            AddGroceryBottomSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp * 0.75F.dp),
                searchInput = searchInput,
                onSearchInputChanged = { onIntent(GroceryListScreenIntent.UpdateSearchInput(it)) },
                useExpandedPlaceholderText = addGroceryBottomSheetState.useExpandedPlaceHolderText,
                clearSearchInputButtonIsShown = clearSearchInputButtonIsShown,
                showCancelButtonInsteadOfFab = addGroceryBottomSheetState.showCancelButtonInsteadOfFab,
                grocerySearchResults = grocerySearchResults,
                handleBackButtonPress = addGroceryBottomSheetState.handleBackButtonPress,
                onGrocerySearchResultClick = {
                    onIntent(GroceryListScreenIntent.OnGrocerySearchResultClick(it))
                },
                previousGrocery = previousGrocery,
                onSearchFieldFocusChanged = addGroceryBottomSheetState::onSearchFieldFocusChanged,
                onBackButtonClicked = addGroceryBottomSheetState::onSheetDismissed,
                onKeyboardDone = {
                    addGroceryBottomSheetState.onKeyboardDone()
                    onIntent(GroceryListScreenIntent.OnSearchInputKeyboardDone)
                },
                cancelButtonOnClick = addGroceryBottomSheetState::onCancelButtonClicked,
                fabOnClick = addGroceryBottomSheetState::onFabClicked,
                clearSearchInput = { onIntent(GroceryListScreenIntent.OnClearSearchInput) },
                contentType = bottomSheetContentType,
                showExtendedContent = addGroceryBottomSheetState.showExtendedContent,
                editGroceryOnClick = {
                    onIntent(GroceryListScreenIntent.OnEditGroceryClick(it))
                    editGroceryBottomSheetIsVisible = true
                },
                focusRequester = searchBarFocusRequester,
                customProduct = customProduct,
                onCustomProductClick = { product ->
                    onIntent(GroceryListScreenIntent.OnCustomProductClick(product))
                },
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
            val toolbarEnterTransition = remember {
                expandVertically(
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = LinearEasing,
                    )
                )
            }
            val toolbarExitTransition = remember {
                shrinkVertically(
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = LinearEasing,
                    )
                )
            }

            AnimatedVisibility(
                visible = toolbarIsHidden,
                enter = toolbarEnterTransition,
                exit = toolbarExitTransition,
            ) {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )
            }

            AnimatedVisibility(
                visible = !toolbarIsHidden,
                enter = toolbarEnterTransition,
                exit = toolbarExitTransition,
            ) {
                GroceryListCollapsingToolbar(
                    listName = listName,
                    toolbarHeightRange = toolbarHeightRange,
                    toolbarState = toolbarState,
                )
            }
            GroupedLazyGroceryGrid(
                modifier = Modifier
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
                                onClick = {
                                    onIntent(
                                        GroceryListScreenIntent.OnGroceryItemClick(
                                            grocery
                                        )
                                    )
                                },
                                onLongClick = {
                                    editGroceryBottomSheetIsVisible = true
                                    onIntent(GroceryListScreenIntent.OnEditGroceryClick(grocery))
                                }
                            ),
                        groceryName = grocery.name,
                        groceryDescription = grocery.description,
                        color = if (grocery.purchased) {
                            MaterialTheme.colorScheme.groceryListItemColors.purchasedBackgroundColor
                        } else {
                            MaterialTheme.colorScheme.groceryListItemColors.defaultBackgroundColor
                        },
                        groceryIcon = grocery.iconBitmap,
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
        toolbarHeightRange = toolbarHeightRange,
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
                    GroceryPresentation(
                        productId = index.toString(),
                        name = "Grocery $index",
                        purchased = Random.nextBoolean(),
                        description = "Description $index",
                        category = Category(
                            id = index.toString(),
                            name = "Category$index",
                            sortingPriority = index,
                        ),
                    )
                }
            )
        )
    }

    GroceryGeniusTheme {
        GroceryListScreen(
            listName = "Grocery List",
            searchInput = "",
            groceries = dummyGroceries,
            grocerySearchResults = emptyList(),
            clearSearchInputButtonIsShown = false,
            bottomSheetContentType = BottomSheetContentType.Suggestions,
            previousGrocery = null,
            editGrocery = null,
            clearEditGroceryDescriptionButtonIsShown = false,
            editGroceryDescription = null,
            onIntent = {},
            customProduct = null,
        )
    }
}
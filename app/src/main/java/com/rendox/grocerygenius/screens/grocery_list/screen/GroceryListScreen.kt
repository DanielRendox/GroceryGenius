package com.rendox.grocerygenius.screens.grocery_list.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.screens.grocery_list.bottom_sheet.AddGroceryBottomSheetContent
import com.rendox.grocerygenius.screens.grocery_list.bottom_sheet.BottomSheetContentType
import com.rendox.grocerygenius.screens.grocery_list.bottom_sheet.SheetDragHandle
import com.rendox.grocerygenius.screens.grocery_list.bottom_sheet.rememberAddGroceryBottomSheetState
import com.rendox.grocerygenius.ui.components.Scrim
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbar
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbarScaffoldScrollableState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.CollapsingToolbarNestedScrollConnection
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.ToolbarState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.rememberExitUntilCollapsedToolbarState
import com.rendox.grocerygenius.ui.components.grocery_list.GroceryGroup
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGrid
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGridItem
import kotlinx.coroutines.cancelChildren

@Composable
fun GroceryListRoute(
    modifier: Modifier = Modifier,
    viewModel: GroceryListScreenViewModel = viewModel(),
) {
    val groceries by viewModel.groceriesFlow.collectAsState()
    val grocerySearchResults by viewModel.grocerySearchResultsFlow.collectAsState()
    val clearSearchInputButtonIsShown by viewModel.clearSearchInputButtonIsShownFlow.collectAsState()
    val bottomSheetContentType by viewModel.bottomSheetContentTypeFlow.collectAsState()
    val previousGroceryName by viewModel.previousGroceryName.collectAsState()

    GroceryListScreen(
        modifier = modifier,
        listName = "Grocery List",
        groceries = groceries,
        searchInput = viewModel.searchInput ?: "",
        grocerySearchResults = grocerySearchResults,
        clearSearchInputButtonIsShown = clearSearchInputButtonIsShown,
        bottomSheetContentType = bottomSheetContentType,
        previousGroceryName = previousGroceryName,
        onGroceryItemClick = viewModel::toggleItemPurchased,
        updateSearchInput = viewModel::updateSearchInput,
        onGrocerySearchResultClick = viewModel::onGrocerySearchResultClick,
        onSearchInputKeyboardDone = viewModel::onSearchInputKeyboardDone,
        clearSearchInput = viewModel::onClearSearchInput,
        onBottomSheetCollapsing = viewModel::onBottomSheetCollapsing,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroceryListScreen(
    modifier: Modifier = Modifier,
    listName: String,
    searchInput: String,
    groceries: List<GroceryGroup>,
    grocerySearchResults: List<GroceryGroup>,
    clearSearchInputButtonIsShown: Boolean,
    bottomSheetContentType: BottomSheetContentType,
    previousGroceryName: String?,
    onGroceryItemClick: (Grocery) -> Unit,
    updateSearchInput: (String) -> Unit,
    onGrocerySearchResultClick: (Grocery) -> Unit,
    onSearchInputKeyboardDone: () -> Unit,
    clearSearchInput: () -> Unit,
    onBottomSheetCollapsing: () -> Unit,
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
    val bottomSheetState = rememberStandardBottomSheetState()
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)
    val addGroceryBottomSheetState =
        rememberAddGroceryBottomSheetState(bottomSheetState, searchInput)

    LaunchedEffect(addGroceryBottomSheetState.sheetIsCollapsing) {
        if (addGroceryBottomSheetState.sheetIsCollapsing) {
            onBottomSheetCollapsing()
        }
    }

    BottomSheetScaffold(
        modifier = modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars
            ),
        sheetContent = {
            AddGroceryBottomSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp * 0.75F.dp)
                    .windowInsetsPadding(
                        WindowInsets.navigationBars
                    ),
                searchInput = searchInput,
                onSearchInputChanged = updateSearchInput,
                useExpandedPlaceholderText = addGroceryBottomSheetState.useExpandedPlaceHolderText,
                clearSearchInputButtonIsShown = clearSearchInputButtonIsShown,
                showCancelButtonInsteadOfFab = addGroceryBottomSheetState.showCancelButtonInsteadOfFab,
                grocerySearchResults = grocerySearchResults,
                handleBackButtonPress = addGroceryBottomSheetState.handleBackButtonPress,
                changeSearchFieldFocusEvent = addGroceryBottomSheetState.changeSearchFieldFocusEvent,
                onGrocerySearchResultClick = onGrocerySearchResultClick,
                previousGroceryName = previousGroceryName,
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
            )
        },
        sheetDragHandle = {
            SheetDragHandle(modifier = Modifier.padding(vertical = 12.dp))
        },
        sheetPeekHeight = 100.dp + navigationBarHeight,
        scaffoldState = scaffoldState,
    ) { paddingValues ->
        val hideToolbarOffset by animateFloatAsState(
            targetValue = if (bottomSheetState.targetValue == SheetValue.Expanded) {
                toolbarState.height + WindowInsets.statusBars.getTop(LocalDensity.current)
            } else {
                0F
            },
            label = "animateFloatAsState",
            animationSpec = tween(
                easing = LinearEasing,
                durationMillis = 200,
            ),
        )
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(nestedScrollConnection)
        ) {
            LazyGroceryGrid(
                modifier = Modifier
                    .statusBarsPadding()
                    .graphicsLayer {
                        translationY =
                            toolbarState.height + toolbarState.offset - hideToolbarOffset
                    }
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
                        modifier = Modifier.fillMaxSize(),
                        grocery = grocery,
                        onClick = {
                            onGroceryItemClick(grocery)
                        },
                    )
                },
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = collapsedToolbarHeight + 16.dp,
                ),
            )

            GroceryListCollapsingToolbar(
                modifier = Modifier.graphicsLayer {
                    translationY = toolbarState.offset - hideToolbarOffset
                },
                listName = listName,
                toolbarHeightRange = toolbarHeightRange,
                toolbarState = toolbarState,
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        if (bottomSheetState.targetValue == SheetValue.Expanded) {
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
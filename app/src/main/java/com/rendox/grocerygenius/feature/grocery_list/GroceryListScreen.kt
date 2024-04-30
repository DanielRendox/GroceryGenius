package com.rendox.grocerygenius.feature.grocery_list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.feature.add_grocery.AddGroceryBottomSheetContent
import com.rendox.grocerygenius.feature.add_grocery.AddGroceryBottomSheetState
import com.rendox.grocerygenius.feature.add_grocery.AddGroceryUiIntent
import com.rendox.grocerygenius.feature.add_grocery.AddGroceryUiState
import com.rendox.grocerygenius.feature.add_grocery.AddGroceryViewModel
import com.rendox.grocerygenius.feature.add_grocery.rememberAddGroceryBottomSheetState
import com.rendox.grocerygenius.feature.edit_grocery.EditGroceryBottomSheet
import com.rendox.grocerygenius.feature.edit_grocery.EditGroceryUiIntent
import com.rendox.grocerygenius.feature.edit_grocery.EditGroceryViewModel
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.model.GroceryGeniusColorScheme
import com.rendox.grocerygenius.ui.components.BottomSheetDragHandle
import com.rendox.grocerygenius.ui.components.DeleteConfirmationDialog
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
import com.rendox.grocerygenius.ui.theme.GroceryItemRounding
import com.rendox.grocerygenius.ui.theme.TopAppBarMediumHeight
import com.rendox.grocerygenius.ui.theme.TopAppBarSmallHeight
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListRoute(
    groceryListViewModel: GroceryListViewModel = hiltViewModel(),
    addGroceryViewModel: AddGroceryViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToCategoryScreen: () -> Unit,
) {
    val groceryGroups by groceryListViewModel.groceryGroupsFlow.collectAsStateWithLifecycle()
    val closeGroceryListScreenEvent by groceryListViewModel.closeGroceryListScreenEvent.collectAsStateWithLifecycle()
    val addGroceryUiState by addGroceryViewModel.uiStateFlow.collectAsStateWithLifecycle()
    val openedGroceryListId = groceryListViewModel.openedGroceryListId
    val groceryListEditModeIsEnabled by groceryListViewModel.groceryListEditModeIsEnabledFlow.collectAsStateWithLifecycle()
    val categories by groceryListViewModel.categoriesFlow.collectAsStateWithLifecycle()
    val navigateToCategoryScreenEvent by groceryListViewModel.navigateToCategoryScreenEvent.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    ObserveUiEvent(closeGroceryListScreenEvent) {
        navigateBack()
    }
    ObserveUiEvent(navigateToCategoryScreenEvent) {
        navigateToCategoryScreen()
    }

    val addBottomSheetState = rememberStandardBottomSheetState()
    val scaffoldState = rememberBottomSheetScaffoldState(addBottomSheetState)
    val addGroceryBottomSheetState = rememberAddGroceryBottomSheetState(addBottomSheetState)

    LaunchedEffect(addGroceryBottomSheetState.sheetIsCollapsing) {
        if (addGroceryBottomSheetState.sheetIsCollapsing) {
            addGroceryViewModel.onIntent(AddGroceryUiIntent.OnAddGroceryBottomSheetCollapsing)
        }
    }
    LaunchedEffect(addGroceryBottomSheetState.sheetIsFullyExpanded, openedGroceryListId) {
        if (addGroceryBottomSheetState.sheetIsFullyExpanded) {
            addGroceryViewModel.onIntent(
                AddGroceryUiIntent.OnAddGroceryBottomSheetExpanded(openedGroceryListId)
            )
        }
    }

    val editBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var editGroceryScreenIsVisible by rememberSaveable { mutableStateOf(false) }
    val editGroceryIdState = rememberSaveable { mutableStateOf<String?>(null) }

    GroceryListScreen(
        modifier = Modifier.fillMaxSize(),
        groceryGroups = groceryGroups,
        searchQuery = addGroceryViewModel.searchQuery,
        onGroceryListUiIntent = groceryListViewModel::onIntent,
        scaffoldState = scaffoldState,
        addGroceryBottomSheetState = addGroceryBottomSheetState,
        showEditGroceryBottomSheet = {
            editGroceryIdState.value = it
            editGroceryScreenIsVisible = true
        },
        scrimIsShown = addGroceryBottomSheetState.sheetIsExpanding ||
                editBottomSheetState.targetValue == SheetValue.Expanded,
        toolbarIsHidden = addGroceryBottomSheetState.sheetIsExpanding ||
                editBottomSheetState.targetValue == SheetValue.Expanded,
        groceryListName = groceryListViewModel.openedGroceryListName,
        navigateBack = navigateBack,
        onAddGroceryUiIntent = addGroceryViewModel::onIntent,
        addGroceryUiState = addGroceryUiState,
        groceryListEditModeIsEnabled = groceryListEditModeIsEnabled,
        categories = categories,
        navigateToCategoryScreen = { categoryId ->
            groceryListViewModel.onIntent(
                GroceryListsUiIntent.OnNavigateToCategoryScreen(categoryId)
            )
        },
    )

    val editGroceryId = editGroceryIdState.value
    if (editGroceryScreenIsVisible && editGroceryId != null) {
        val editGroceryViewModel: EditGroceryViewModel = hiltViewModel()
        val editGroceryScreenState by editGroceryViewModel.uiStateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(editGroceryIdState, openedGroceryListId) {
            editGroceryViewModel.onIntent(
                EditGroceryUiIntent.OnEditOtherGrocery(
                    productId = editGroceryId,
                    groceryListId = openedGroceryListId,
                )
            )
        }

        EditGroceryBottomSheet(
            modifier = Modifier.fillMaxSize(),
            screenState = editGroceryScreenState,
            editGroceryDescription = editGroceryViewModel.editGroceryDescription,
            editBottomSheetState = editBottomSheetState,
            hideBottomSheet = {
                coroutineScope
                    .launch { editBottomSheetState.hide() }
                    .invokeOnCompletion {
                        if (!editBottomSheetState.isVisible) {
                            editGroceryScreenIsVisible = false
                        }
                    }
            },
            onIntent = editGroceryViewModel::onIntent,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun GroceryListScreen(
    modifier: Modifier = Modifier,
    groceryGroups: List<GroceryGroup>,
    searchQuery: String,
    addGroceryUiState: AddGroceryUiState,
    groceryListName: TextFieldValue = TextFieldValue(""),
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    addGroceryBottomSheetState: AddGroceryBottomSheetState = rememberAddGroceryBottomSheetState(),
    groceryListEditModeIsEnabled: Boolean = false,
    scrimIsShown: Boolean = false,
    toolbarIsHidden: Boolean = false,
    categories: List<Category> = emptyList(),
    navigateBack: () -> Unit = {},
    onGroceryListUiIntent: (GroceryListsUiIntent) -> Unit = {},
    onAddGroceryUiIntent: (AddGroceryUiIntent) -> Unit = {},
    showEditGroceryBottomSheet: (String) -> Unit = {},
    navigateToCategoryScreen: (String) -> Unit = {},
) {
    val toolbarHeightRange = with(LocalDensity.current) {
        TopAppBarSmallHeight.roundToPx()..TopAppBarMediumHeight.roundToPx()
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

    val imeIsVisible = WindowInsets.isImeVisible
    LaunchedEffect(imeIsVisible) {
        if (!imeIsVisible) onGroceryListUiIntent(GroceryListsUiIntent.OnKeyboardHidden)
    }

    var deleteGroceryListDialogIsShown by remember { mutableStateOf(false) }
    if (deleteGroceryListDialogIsShown) {
        DeleteConfirmationDialog(
            bodyText = stringResource(id = R.string.delete_grocery_list_dialog_text),
            onConfirm = {
                deleteGroceryListDialogIsShown = false
                onGroceryListUiIntent(GroceryListsUiIntent.OnDeleteGroceryList)
            },
            onDismissRequest = { deleteGroceryListDialogIsShown = false },
        )
    }

    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    BottomSheetScaffold(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
            .consumeWindowInsets(WindowInsets.navigationBars),
        sheetContent = {
            BackHandler(
                enabled = addGroceryBottomSheetState.handleBackButtonPress,
                onBack = { addGroceryBottomSheetState.onSheetDismissed() }
            )
            AddGroceryBottomSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp * 0.75F.dp)
                    .navigationBarsPadding(),
                searchQuery = searchQuery,
                contentType = addGroceryUiState.bottomSheetContentType,
                clearSearchQueryButtonIsShown = addGroceryUiState.clearSearchQueryButtonIsShown,
                customProduct = addGroceryUiState.customProduct,
                grocerySearchResults = addGroceryUiState.grocerySearchResults,
                previousGrocery = addGroceryUiState.previouslyAddedGrocery,
                showExtendedContent = addGroceryBottomSheetState.showExtendedContent,
                useExpandedPlaceholderText = addGroceryBottomSheetState.useExpandedPlaceholderText,
                changeSearchFieldFocusEvent = addGroceryBottomSheetState.changeSearchFieldFocusEvent,
                showCancelButtonInsteadOfFab = addGroceryBottomSheetState.showCancelButtonInsteadOfFab,
                onKeyboardDone = {
                    addGroceryBottomSheetState.onKeyboardDone()
                    onAddGroceryUiIntent(AddGroceryUiIntent.OnSearchFieldKeyboardDone)
                },
                onCancelButtonClicked = addGroceryBottomSheetState::onCancelButtonClicked,
                onFabClicked = addGroceryBottomSheetState::onFabClicked,
                onSearchFieldFocusChanged = addGroceryBottomSheetState::onSearchFieldFocusChanged,
                onSearchQueryChanged = {
                    onAddGroceryUiIntent(AddGroceryUiIntent.OnUpdateSearchQuery(it))
                },
                onClearSearchQuery = {
                    onAddGroceryUiIntent(AddGroceryUiIntent.OnClearSearchQuery)
                },
                onGrocerySearchResultClick = {
                    onAddGroceryUiIntent(AddGroceryUiIntent.OnGrocerySearchResultClick(it))
                },
                onCustomProductClick = {
                    onAddGroceryUiIntent(AddGroceryUiIntent.OnCustomProductClick)
                },
                onEditGroceryClicked = {
                    addGroceryUiState.previouslyAddedGrocery?.let {
                        showEditGroceryBottomSheet(it.productId)
                    }
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
            GroceryListCollapsingToolbar(
                listName = groceryListName,
                toolbarHeightRange = toolbarHeightRange,
                toolbarState = toolbarState,
                toolbarIsHidden = toolbarIsHidden,
                onUpdateGroceryListName = {
                    onGroceryListUiIntent(GroceryListsUiIntent.UpdateGroceryListName(it))
                },
                editModeIsEnabled = groceryListEditModeIsEnabled,
                onNavigationIconClicked = navigateBack,
                onDeleteGroceryList = {
                    deleteGroceryListDialogIsShown = true
                },
                onEditGroceryListToggle = {
                    onGroceryListUiIntent(GroceryListsUiIntent.OnEditGroceryListToggle(it))
                },
            )

            GroceryGrid(
                modifier = Modifier.fillMaxSize(),
                groceryGroups = groceryGroups,
                onGroceryClick = { onGroceryListUiIntent(GroceryListsUiIntent.OnGroceryItemClick(it)) },
                categories = categories,
                onGroceryLongClick = {
                    showEditGroceryBottomSheet(it.productId)
                },
                lazyGridState = lazyGridState,
                onCategoryItemClick = navigateToCategoryScreen,
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        if (scrimIsShown) {
            Scrim(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { addGroceryBottomSheetState.onSheetDismissed() },
                )
            )
        }
    }
}

@Composable
private fun GroceryListCollapsingToolbar(
    modifier: Modifier = Modifier,
    listName: TextFieldValue,
    editModeIsEnabled: Boolean,
    toolbarIsHidden: Boolean,
    toolbarHeightRange: IntRange,
    toolbarState: ToolbarState,
    onUpdateGroceryListName: (TextFieldValue) -> Unit,
    onNavigationIconClicked: () -> Unit = {},
    onDeleteGroceryList: () -> Unit = {},
    onEditGroceryListToggle: (Boolean) -> Unit = {},
) {
    Column(modifier = modifier) {
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

        val listNameFieldFocusRequester = remember(editModeIsEnabled) {
            if (editModeIsEnabled) FocusRequester() else null
        }
        LaunchedEffect(editModeIsEnabled) {
            listNameFieldFocusRequester?.requestFocus()
        }

        AnimatedVisibility(
            visible = !toolbarIsHidden,
            enter = toolbarEnterTransition,
            exit = toolbarExitTransition,
        ) {
            val expandedTitleStyle = MaterialTheme.typography.headlineSmall
            CollapsingToolbar(
                modifier = Modifier.graphicsLayer {
                    translationY = toolbarState.offset
                },
                titleExpanded = {
                    GroceryListTitleField(
                        listName = listName,
                        listNameFieldFocusRequester = listNameFieldFocusRequester,
                        listNameFieldIsEditable = editModeIsEnabled,
                        textStyle = expandedTitleStyle,
                        onUpdateGroceryListName = onUpdateGroceryListName,
                    )
                },
                expandedTitleFontSize = expandedTitleStyle.fontSize,
                titleBottomPadding = 24.dp,
                toolbarState = toolbarState,
                toolbarHeightRange = toolbarHeightRange,
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onDeleteGroceryList) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                    IconButton(onClick = { onEditGroceryListToggle(!editModeIsEnabled) }) {
                        if (editModeIsEnabled) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = stringResource(R.string.done)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit)
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun GroceryListTitleField(
    modifier: Modifier = Modifier,
    listName: TextFieldValue,
    listNameFieldFocusRequester: FocusRequester?,
    listNameFieldIsEditable: Boolean,
    textStyle: TextStyle,
    onUpdateGroceryListName: (TextFieldValue) -> Unit,
) {
    Box(modifier = modifier.padding(end = 16.dp)) {
        val textColor = MaterialTheme.colorScheme.onSurface
        if (listNameFieldIsEditable) {
            BasicTextField(
                modifier = Modifier.focusRequester(listNameFieldFocusRequester!!),
                value = listName,
                onValueChange = onUpdateGroceryListName,
                singleLine = true,
                textStyle = textStyle.copy(
                    textMotion = TextMotion.Animated,
                    color = textColor,
                ),
                cursorBrush = SolidColor(textColor),
            )
        } else {
            Text(
                text = listName.text,
                style = textStyle.copy(
                    textMotion = TextMotion.Animated,
                    color = textColor,
                ),
                maxLines = 1,
                color = textColor,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (listName.text.isEmpty()) {
            Text(
                text = stringResource(R.string.grocery_list_name_text_field_placeholder),
                style = textStyle.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GroceryGrid(
    modifier: Modifier = Modifier,
    groceryGroups: List<GroceryGroup>,
    lazyGridState: LazyGridState,
    categories: List<Category>,
    onGroceryClick: (Grocery) -> Unit,
    onGroceryLongClick: (Grocery) -> Unit,
    onCategoryItemClick: (String) -> Unit,
) {
    val context = LocalContext.current

    GroupedLazyGroceryGrid(
        modifier = modifier,
        groceryGroups = groceryGroups,
        groceryItem = { grocery ->
            LazyGroceryGridItem(
                modifier = Modifier
                    .fillMaxSize()
                    .combinedClickable(
                        onClick = { onGroceryClick(grocery) },
                        onLongClick = { onGroceryLongClick(grocery) },
                    ),
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
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp,
        ),
        lazyGridState = lazyGridState,
        numOfAdditionalItems = categories.size + 1,
        additionalItems = {
            val categoryContentType = "Category"
            val spacerContentType = "SpacerBetweenListAndCategories"
            item(
                key = spacerContentType,
                contentType = spacerContentType,
                span = { GridItemSpan(maxLineSpan) },
            ) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(
                count = categories.size,
                key = { index -> categories[index].id },
                contentType = { categoryContentType },
                span = { GridItemSpan(maxLineSpan) },
            ) { index ->
                CategoryItem(
                    modifier = Modifier
                        .clip(
                            shape = RoundedCornerShape(
                                topStart = if (index == 0) GroceryItemRounding else 0.dp,
                                topEnd = if (index == 0) GroceryItemRounding else 0.dp,
                                bottomStart = if (index == categories.lastIndex) GroceryItemRounding else 0.dp,
                                bottomEnd = if (index == categories.lastIndex) GroceryItemRounding else 0.dp,
                            )
                        )
                        .clickable { onCategoryItemClick(categories[index].id) },
                    name = categories[index].name,
                )
            }
        }
    )
}

val sampleGroceryGroups = listOf(
    GroceryGroup(
        titleId = null,
        groceries = List(5) {
            Grocery(
                name = "Not purchased grocery $it",
                purchased = false,
                productId = "not_purchased_$it"
            )
        }
    ),
    GroceryGroup(
        titleId = R.string.purchased_groceries_group_title,
        groceries = List(10) {
            Grocery(
                name = "Purchased grocery $it",
                purchased = true,
                productId = "purchased_$it"
            )
        }
    ),
)

@Composable
private fun CategoryItem(
    modifier: Modifier = Modifier,
    name: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondaryContainer),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .padding(16.dp)
                .weight(1F),
            text = name,
        )
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            contentDescription = stringResource(R.string.forward),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun GroceryListScreenPreview() {
    GroceryGeniusTheme(requestedColorScheme = GroceryGeniusColorScheme.YellowColorScheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            GroceryListScreen(
                groceryGroups = sampleGroceryGroups,
                searchQuery = "",
                addGroceryUiState = AddGroceryUiState(),
            )
        }
    }
}

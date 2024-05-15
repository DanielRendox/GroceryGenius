package com.rendox.grocerygenius.feature.settings

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.feature.settings.categories.recyclerview.CategoriesRecyclerViewAdapter
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.model.DarkThemeConfig
import com.rendox.grocerygenius.model.GroceryGeniusColorScheme
import com.rendox.grocerygenius.model.GroceryList
import com.rendox.grocerygenius.model.UserPreferences
import com.rendox.grocerygenius.ui.components.BottomSheetDragHandle
import com.rendox.grocerygenius.ui.components.CustomIconSetting
import com.rendox.grocerygenius.ui.components.DropDownMenuToggleIcon
import com.rendox.grocerygenius.ui.components.LazyDropdownMenu
import com.rendox.grocerygenius.ui.components.TonalDataInput
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbar
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbarScaffoldScrollableState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.CollapsingToolbarNestedScrollConnection
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.rememberExitUntilCollapsedToolbarState
import com.rendox.grocerygenius.ui.helpers.ObserveUiEvent
import com.rendox.grocerygenius.ui.helpers.UiEvent
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme
import com.rendox.grocerygenius.ui.theme.TopAppBarMediumHeight
import com.rendox.grocerygenius.ui.theme.TopAppBarSmallHeight
import com.rendox.grocerygenius.ui.theme.deriveColorScheme
import kotlinx.coroutines.launch

@Composable
fun SettingsRoute(
    viewModel: SettingsScreenViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {},
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val showDynamicColorNotSupportedMessage by viewModel.showDynamicColorNotSupportedMessage.collectAsStateWithLifecycle()
    SettingsScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        showDynamicColorNotSupportedMessage = showDynamicColorNotSupportedMessage,
        onIntent = viewModel::onIntent,
        navigateBack = navigateBack,
    )
}

@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    uiState: SettingsScreenState,
    showDynamicColorNotSupportedMessage: UiEvent<Unit>?,
    onIntent: (SettingsScreenIntent) -> Unit,
    navigateBack: () -> Unit,
) {
    var isThemeDropdownExpanded by remember { mutableStateOf(false) }
    var isDefaultListDropdownExpanded by remember { mutableStateOf(false) }

    val toolbarHeightRange = with(LocalDensity.current) {
        TopAppBarSmallHeight.roundToPx()..TopAppBarMediumHeight.roundToPx()
    }
    val toolbarState = rememberExitUntilCollapsedToolbarState(toolbarHeightRange)
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val scrollState: CollapsingToolbarScaffoldScrollableState = remember {
        object : CollapsingToolbarScaffoldScrollableState, ScrollableState by lazyListState {
            override val firstVisibleItemIndex: Int
                get() = lazyListState.firstVisibleItemIndex
            override val firstVisibleItemScrollOffset: Int
                get() = lazyListState.firstVisibleItemScrollOffset
        }
    }

    val nestedScrollConnection = remember {
        CollapsingToolbarNestedScrollConnection(
            toolbarState = toolbarState,
            scrollState = scrollState,
            coroutineScope = coroutineScope,
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val dynamicColorNotSupportedMessage =
        stringResource(R.string.settings_dynamic_color_not_supported_message)
    ObserveUiEvent(showDynamicColorNotSupportedMessage) {
        snackbarHostState.showSnackbar(message = dynamicColorNotSupportedMessage)
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.navigationBars,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .nestedScroll(nestedScrollConnection)
        ) {
            val titleStyle = MaterialTheme.typography.headlineSmall
            CollapsingToolbar(
                toolbarState = toolbarState,
                toolbarHeightRange = toolbarHeightRange,
                titleExpanded = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = titleStyle.copy(textMotion = TextMotion.Animated),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                expandedTitleFontSize = titleStyle.fontSize,
                titleBottomPadding = 24.dp,
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )

            if (!uiState.isLoading) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    item {
                        SettingsTitle(
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                            title = stringResource(R.string.settings_theme)
                        )
                    }
                    item {
                        DarkThemeConfigSetting(
                            modifier = Modifier.padding(vertical = 16.dp),
                            darkThemeConfig = uiState.userPreferences.darkThemeConfig,
                            isThemeDropdownExpanded = isThemeDropdownExpanded,
                            onChangeDarkThemeConfig = {
                                onIntent(SettingsScreenIntent.ChangeDarkThemeConfig(it))
                            },
                            onThemeDropdownExpandedChanged = { isThemeDropdownExpanded = it }
                        )
                    }
                    item {
                        SystemAccentColorSetting(
                            useSystemAccentColor = uiState.userPreferences.useSystemAccentColor,
                            onUseSystemAccentColorChanged = {
                                onIntent(SettingsScreenIntent.ChangeUseSystemAccentColor(it))
                            }
                        )
                    }
                    item {
                        AnimatedVisibility(visible = !uiState.userPreferences.useSystemAccentColor) {
                            ColorSchemePicker(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 40.dp,
                                        end = 16.dp,
                                        top = 8.dp,
                                        bottom = 8.dp,
                                    ),
                                useDarkTheme = when (uiState.userPreferences.darkThemeConfig) {
                                    DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                                    DarkThemeConfig.LIGHT -> false
                                    DarkThemeConfig.DARK -> true
                                },
                                selectedTheme = uiState.userPreferences.selectedTheme,
                                onSchemeSelected = {
                                    onIntent(SettingsScreenIntent.ChangeColorScheme(it))
                                },
                            )
                        }
                    }
                    item {
                        SettingsTitle(
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                            title = stringResource(R.string.settings_preferences)
                        )
                    }
                    item {
                        OpenLastViewedListSetting(
                            openLastViewedList = uiState.userPreferences.openLastViewedList,
                            onChangeOpenLastViewedListConfig = {
                                onIntent(SettingsScreenIntent.ChangeOpenLastViewedListConfig(it))
                            }
                        )
                    }
                    item {
                        AnimatedVisibility(visible = !uiState.userPreferences.openLastViewedList) {
                            DefaultListSetting(
                                groceryLists = uiState.groceryLists,
                                defaultListId = uiState.userPreferences.defaultListId,
                                isDefaultListDropdownExpanded = isDefaultListDropdownExpanded,
                                onChangeDefaultList = {
                                    onIntent(SettingsScreenIntent.OnChangeDefaultList(it))
                                },
                                onDefaultListDropdownExpandedChanged = {
                                    isDefaultListDropdownExpanded = it
                                }
                            )
                        }
                    }
                    item {
                        CategoriesOrderSetting(
                            categories = uiState.categories,
                            updateCategories = { categories ->
                                onIntent(SettingsScreenIntent.OnUpdateCategories(categories))
                            },
                            onResetCategoriesOrder = {
                                onIntent(SettingsScreenIntent.OnResetCategoriesOrder)
                            }
                        )
                    }
                    item {
                        SettingsTitle(
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                            title = stringResource(R.string.settings_feedback)
                        )
                    }
                    item {
                        GitHubLink()
                    }
                    item {
                        EmailLink()
                    }
                    item {
                        SettingsTitle(
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                            title = stringResource(R.string.settings_credits)
                        )
                    }
                    item {
                        FreepikAttribution()
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsTitle(
    modifier: Modifier = Modifier,
    title: String,
) {
    Text(
        modifier = modifier,
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun DarkThemeConfigSetting(
    modifier: Modifier = Modifier,
    darkThemeConfig: DarkThemeConfig,
    isThemeDropdownExpanded: Boolean,
    onChangeDarkThemeConfig: (DarkThemeConfig) -> Unit,
    onThemeDropdownExpandedChanged: (Boolean) -> Unit
) {
    CustomIconSetting(
        modifier = modifier,
        title = stringResource(R.string.settings_theme_mode),
        icon = {
            Icon(
                painterResource(R.drawable.day_night),
                contentDescription = null,
            )
        },
        trailingComponent = {
            val themeModes = DarkThemeConfig.entries.map { it.asLocalString() }
            val selectedOptionIndex = remember(darkThemeConfig) {
                DarkThemeConfig.entries.indexOf(darkThemeConfig)
            }
            TonalDataInput(
                onClick = { onThemeDropdownExpandedChanged(!isThemeDropdownExpanded) },
                indication = null,
                dropDownMenu = {
                    DropdownMenu(
                        expanded = isThemeDropdownExpanded,
                        onDismissRequest = { onThemeDropdownExpandedChanged(false) }
                    ) {
                        themeModes.forEachIndexed { index, themeMode ->
                            DropdownMenuItem(
                                onClick = {
                                    onChangeDarkThemeConfig(DarkThemeConfig.entries[index])
                                    onThemeDropdownExpandedChanged(false)
                                },
                                text = {
                                    Text(text = themeMode)
                                }
                            )
                        }
                    }
                }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.widthIn(min = 56.dp, max = 136.dp),
                        text = themeModes[selectedOptionIndex],
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                    DropDownMenuToggleIcon(expanded = isThemeDropdownExpanded)
                }
            }
        }
    )
}

@Composable
private fun SystemAccentColorSetting(
    modifier: Modifier = Modifier,
    useSystemAccentColor: Boolean,
    onUseSystemAccentColorChanged: (Boolean) -> Unit
) {
    CustomIconSetting(
        modifier = modifier.clickable {
            onUseSystemAccentColorChanged(!useSystemAccentColor)
        },
        title = stringResource(R.string.settings_use_system_accent_color),
        icon = {
            Icon(
                painterResource(R.drawable.baseline_palette_24),
                contentDescription = null,
            )
        },
        trailingComponent = {
            Switch(
                checked = useSystemAccentColor,
                onCheckedChange = onUseSystemAccentColorChanged,
            )
        }
    )
}

@Composable
fun ColorSchemePicker(
    modifier: Modifier = Modifier,
    useDarkTheme: Boolean,
    selectedTheme: GroceryGeniusColorScheme,
    onSchemeSelected: (GroceryGeniusColorScheme) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = SpaceEvenly,
    ) {
        for (scheme in GroceryGeniusColorScheme.entries) {
            val colors = scheme.deriveColorScheme(useDarkTheme)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color = colors.primaryContainer)
                    .clickable { onSchemeSelected(scheme) },
                contentAlignment = Alignment.Center,
            ) {
                if (scheme == selectedTheme) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = colors.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun OpenLastViewedListSetting(
    modifier: Modifier = Modifier,
    openLastViewedList: Boolean,
    onChangeOpenLastViewedListConfig: (Boolean) -> Unit
) {
    CustomIconSetting(
        modifier = modifier
            .padding(vertical = 6.dp)
            .clickable { onChangeOpenLastViewedListConfig(!openLastViewedList) },
        title = stringResource(R.string.settings_open_last_viewed_list),
        icon = {
            Icon(
                painterResource(id = R.drawable.baseline_history_24),
                contentDescription = null,
            )
        },
        trailingComponent = {
            Switch(
                checked = openLastViewedList,
                onCheckedChange = onChangeOpenLastViewedListConfig
            )
        }
    )
}

@Composable
fun DefaultListSetting(
    modifier: Modifier = Modifier,
    groceryLists: List<GroceryList>,
    defaultListId: String? = null,
    isDefaultListDropdownExpanded: Boolean,
    onChangeDefaultList: (String?) -> Unit,
    onDefaultListDropdownExpandedChanged: (Boolean) -> Unit
) {
    CustomIconSetting(
        modifier = modifier.padding(vertical = 6.dp),
        title = stringResource(R.string.settings_default_list),
        icon = {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
            )
        },
        trailingComponent = {
            val unspecifiedListTitle = stringResource(R.string.settings_default_list_unspecified)
            val options = listOf(unspecifiedListTitle) + groceryLists.map { it.name }
            TonalDataInput(
                onClick = {
                    onDefaultListDropdownExpandedChanged(!isDefaultListDropdownExpanded)
                },
                indication = null,
                dropDownMenu = {
                    when {
                        options.isEmpty() -> {}
                        options.size <= 6 -> {
                            DropdownMenu(
                                expanded = isDefaultListDropdownExpanded,
                                onDismissRequest = { onDefaultListDropdownExpandedChanged(false) }
                            ) {
                                options.forEachIndexed { index, option ->
                                    DropdownMenuItem(
                                        onClick = {
                                            onChangeDefaultList(
                                                if (index == 0) null else groceryLists[index - 1].id
                                            )
                                            onDefaultListDropdownExpandedChanged(false)
                                        },
                                        text = {
                                            Text(text = option)
                                        }
                                    )
                                }
                            }
                        }

                        else -> {
                            LazyDropdownMenu(
                                expanded = isDefaultListDropdownExpanded,
                                onDismissRequest = { onDefaultListDropdownExpandedChanged(false) },
                                options = options,
                                onOptionSelected = { index ->
                                    val groceryListId =
                                        if (index == 0) null else groceryLists[index - 1].id
                                    groceryListId?.let(onChangeDefaultList)
                                    onDefaultListDropdownExpandedChanged(false)
                                }
                            )
                        }
                    }
                }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.widthIn(min = 56.dp, max = 136.dp),
                        text = remember(groceryLists, defaultListId) {
                            groceryLists.find {
                                it.id == defaultListId
                            }?.name ?: unspecifiedListTitle
                        },
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                    DropDownMenuToggleIcon(expanded = isDefaultListDropdownExpanded)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriesOrderSetting(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    updateCategories: (List<Category>) -> Unit,
    onResetCategoriesOrder: () -> Unit,
) {
    var bottomSheetIsVisible by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val hideBottomSheet = {
        coroutineScope
            .launch { bottomSheetState.hide() }
            .invokeOnCompletion { bottomSheetIsVisible = false }
    }

    CustomIconSetting(
        modifier = modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .clickable { bottomSheetIsVisible = true },
        title = stringResource(R.string.settings_reorder_categories_title),
        icon = {
            Icon(
                painterResource(id = R.drawable.baseline_swap_vert_24),
                contentDescription = null,
            )
        },
        trailingComponent = {
            TonalDataInput(onClick = onResetCategoriesOrder) {
                Text(
                    modifier = Modifier
                        .widthIn(min = 114.dp)
                        .padding(10.dp),
                    text = stringResource(R.string.reset),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    )

    if (bottomSheetIsVisible) {
        ModalBottomSheet(
            onDismissRequest = { hideBottomSheet() },
            sheetState = bottomSheetState,
            dragHandle = { BottomSheetDragHandle() },
            windowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    text = stringResource(R.string.settings_reorder_categories_description),
                    style = MaterialTheme.typography.labelLarge,
                )
                AndroidView(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp),
                    factory = { context ->
                        RecyclerView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                            )
                            layoutManager = LinearLayoutManager(context)
                            this.adapter = CategoriesRecyclerViewAdapter(
                                recyclerView = this,
                                categories = categories,
                                updateLists = updateCategories,
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun GitHubLink(
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    CustomIconSetting(
        modifier = modifier
            .padding(top = 16.dp)
            .clickable { uriHandler.openUri("https://github.com/DanielRendox/GroceryGenius") },
        title = stringResource(R.string.github_link_title),
        description = stringResource(R.string.github_link_description),
        icon = {
            Icon(
                painter = painterResource(R.drawable.github_mark),
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun EmailLink(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val title = stringResource(R.string.email_link_title)
    val developerEmail = "daniel.rendox@gmail.com"
    CustomIconSetting(
        modifier = modifier
            .padding(top = 16.dp)
            .clickable {
                val intent = Intent(
                    Intent.ACTION_SENDTO,
                    Uri.parse("mailto:" + Uri.encode(developerEmail))
                )
                context.startActivity(Intent.createChooser(intent, title))
            },
        title = title,
        description = stringResource(R.string.email_link_description),
        icon = {
            Icon(
                painterResource(R.drawable.mail),
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun FreepikAttribution(
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    CustomIconSetting(
        modifier = modifier
            .padding(top = 16.dp)
            .clickable { uriHandler.openUri("https://www.freepik.com/free-vector/tiny-family-grocery-bag-with-healthy-food-parents-kids-fresh-vegetables-flat-illustration_12291304.htm") },
        title = stringResource(R.string.settings_image_by_freepik_title),
        description = stringResource(R.string.settings_image_by_freepik_description),
        icon = {
            Icon(
                painterResource(R.drawable.image_icon),
                contentDescription = null,
            )
        },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSettingsScreen() {
    GroceryGeniusTheme {
        Surface {
            SettingsScreen(
                uiState = remember {
                    SettingsScreenState(
                        userPreferences = UserPreferences(
                            useSystemAccentColor = false,
                            openLastViewedList = false,
                            selectedTheme = GroceryGeniusColorScheme.YellowColorScheme,
                        ),
                        isLoading = false,
                    )
                },
                onIntent = {},
                navigateBack = {},
                showDynamicColorNotSupportedMessage = null,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ColorSchemePickerPreview() {
    GroceryGeniusTheme {
        Surface(modifier = Modifier.width(400.dp)) {
            ColorSchemePicker(
                modifier = Modifier.padding(16.dp),
                selectedTheme = GroceryGeniusColorScheme.PurpleColorScheme,
                onSchemeSelected = {},
                useDarkTheme = isSystemInDarkTheme()
            )
        }
    }
}

@Composable
private fun DarkThemeConfig.asLocalString() = when (this) {
    DarkThemeConfig.FOLLOW_SYSTEM ->
        stringResource(R.string.settings_dark_theme_config_system_default)

    DarkThemeConfig.LIGHT ->
        stringResource(R.string.settings_dark_theme_config_light)

    DarkThemeConfig.DARK ->
        stringResource(R.string.settings_dark_theme_config_dark)
}

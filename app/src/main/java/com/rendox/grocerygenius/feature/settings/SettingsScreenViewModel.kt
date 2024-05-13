package com.rendox.grocerygenius.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.category.CategoryRepository
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import com.rendox.grocerygenius.data.user_preferences.UserPreferencesRepository
import com.rendox.grocerygenius.ui.helpers.UiEvent
import com.rendox.grocerygenius.ui.theme.dynamicColorIsSupported
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    groceryListRepository: GroceryListRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(SettingsScreenState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _showDynamicColorNotSupportedMessage = MutableStateFlow<UiEvent<Unit>?>(null)
    val showDynamicColorNotSupportedMessage = _showDynamicColorNotSupportedMessage.asStateFlow()

    init {
        viewModelScope.launch {
            _uiStateFlow.update {
                SettingsScreenState(
                    groceryLists = groceryListRepository.getAllGroceryLists().first(),
                    categories = categoryRepository.getAllCategories()
                        .map { categories -> categories.sortedBy { it.sortingPriority } }
                        .first(),
                )
            }
            userPreferencesRepository.userPreferencesFlow.collectLatest { userPreferences ->
                val useSystemAccentColor =
                    if (dynamicColorIsSupported) userPreferences.useSystemAccentColor else false
                _uiStateFlow.update { uiState ->
                    uiState.copy(
                        userPreferences = userPreferences.copy(useSystemAccentColor = useSystemAccentColor),
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onIntent(intent: SettingsScreenIntent) = viewModelScope.launch {
        when (intent) {
            is SettingsScreenIntent.ChangeDarkThemeConfig ->
                userPreferencesRepository.updateDarkThemeConfig(intent.config)

            is SettingsScreenIntent.OnChangeDefaultList ->
                userPreferencesRepository.updateDefaultListId(intent.listId)

            is SettingsScreenIntent.ChangeUseSystemAccentColor -> {
                if (dynamicColorIsSupported) {
                    userPreferencesRepository.updateUseSystemAccentColor(intent.use)
                } else {
                    _showDynamicColorNotSupportedMessage.update {
                        object : UiEvent<Unit> {
                            override val data = Unit
                            override fun onConsumed() {
                                _showDynamicColorNotSupportedMessage.update { null }
                            }
                        }
                    }
                }
            }

            is SettingsScreenIntent.ChangeColorScheme ->
                userPreferencesRepository.updateSelectedTheme(intent.scheme)

            is SettingsScreenIntent.ChangeOpenLastViewedListConfig -> {
                userPreferencesRepository.updateOpenLastViewedList(intent.openLastViewedList)
                if (!intent.openLastViewedList) {
                    userPreferencesRepository.updateDefaultListId(null)
                }
            }

            is SettingsScreenIntent.OnUpdateCategories -> {
                _uiStateFlow.update { it.copy(categories = intent.categories) }
                categoryRepository.updateCategories(
                    categories = intent.categories.mapIndexed { index, category ->
                        category.copy(sortingPriority = index.toLong())
                    }
                )
            }

            is SettingsScreenIntent.OnResetCategoriesOrder -> {
                val newCategories = _uiStateFlow.value.categories
                    .map { it.copy(sortingPriority = it.defaultSortingPriority) }
                    .sortedBy { it.sortingPriority }
                _uiStateFlow.update { it.copy(categories = newCategories) }
                categoryRepository.updateCategories(newCategories)
            }
        }
    }
}
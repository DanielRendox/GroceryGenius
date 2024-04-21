package com.rendox.grocerygenius.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import com.rendox.grocerygenius.data.user_preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    groceryListRepository: GroceryListRepository,
) : ViewModel() {

    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow
    private val groceryListsFlow = groceryListRepository.getAllGroceryLists()
    val screenStateFlow = combine(
        userPreferencesFlow,
        groceryListsFlow
    ) { userPreferences, groceryLists ->
        SettingsScreenState(
            userPreferences = userPreferences,
            groceryLists = groceryLists,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = SettingsScreenState(),
        started = SharingStarted.WhileSubscribed(5000),
    )

    fun onIntent(intent: SettingsScreenIntent) = viewModelScope.launch {
        when (intent) {
            is SettingsScreenIntent.ChangeDarkThemeConfig ->
                userPreferencesRepository.updateDarkThemeConfig(intent.config)

            is SettingsScreenIntent.OnChangeDefaultList ->
                userPreferencesRepository.updateDefaultListId(intent.listId)

            is SettingsScreenIntent.ChangeUseSystemAccentColor ->
                userPreferencesRepository.updateUseSystemAccentColor(intent.use)

            is SettingsScreenIntent.ChangeColorScheme ->
                userPreferencesRepository.updateSelectedTheme(intent.scheme)

            is SettingsScreenIntent.ChangeOpenLastViewedListConfig -> {
                userPreferencesRepository.updateOpenLastViewedList(intent.openLastViewedList)
                if (!intent.openLastViewedList) {
                    userPreferencesRepository.updateDefaultListId(null)
                }
            }
        }
    }
}
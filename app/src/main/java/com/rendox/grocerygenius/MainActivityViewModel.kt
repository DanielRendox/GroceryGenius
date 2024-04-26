package com.rendox.grocerygenius

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.user_preferences.UserPreferencesRepository
import com.rendox.grocerygenius.screens.grocery_list.GROCERY_LIST_ID_ARG
import com.rendox.grocerygenius.screens.grocery_list.GROCERY_LIST_ROUTE
import com.rendox.grocerygenius.screens.grocery_lists_dashboard.GROCERY_LISTS_DASHBOARD_ROUTE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiStateFlow = MutableStateFlow<MainActivityUiState?>(null)

    init {
        viewModelScope.launch {
            val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow
            // using only the first value because the nav host start destination
            // should be the same throughout the whole app session (until the app is closed)
            val initialUserPreferences = userPreferencesFlow.first()
            val lastOpenedListId = if (initialUserPreferences.openLastViewedList) {
                initialUserPreferences.lastOpenedListId
            } else null
            val defaultListId = lastOpenedListId ?: initialUserPreferences.defaultListId
            uiStateFlow.update {
                MainActivityUiState(
                    startDestinationRoute = if (defaultListId != null) {
                        "$GROCERY_LIST_ROUTE/{$GROCERY_LIST_ID_ARG}"
                    } else {
                        GROCERY_LISTS_DASHBOARD_ROUTE
                    },
                    defaultListId = defaultListId,
                    darkThemeConfig = initialUserPreferences.darkThemeConfig,
                    useSystemAccentColor = initialUserPreferences.useSystemAccentColor,
                    selectedTheme = initialUserPreferences.selectedTheme,
                )
            }
            userPreferencesFlow.collectLatest { userPreferences ->
                uiStateFlow.update { uiState ->
                    uiState?.copy(
                        darkThemeConfig = userPreferences.darkThemeConfig,
                        useSystemAccentColor = userPreferences.useSystemAccentColor,
                        selectedTheme = userPreferences.selectedTheme,
                    )
                }
            }
        }
    }

    fun updateStartDestination(route: String) {
        viewModelScope.launch {
            uiStateFlow.update { it?.copy(startDestinationRoute = route) }
        }
    }
}
package com.rendox.grocerygenius

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.user_preferences.UserPreferencesRepository
import com.rendox.grocerygenius.feature.grocery_list.dashboard_screen.GROCERY_LISTS_DASHBOARD_ROUTE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
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
            val defaultListId = userPreferencesRepository.getGroceryListIdToOpenOnStartup()
            val startDestinationRoute = if (defaultListId != null) {
//                "$GROCERY_LIST_ROUTE/{$GROCERY_LIST_SCREEN_LIST_ID_NAV_ARG}"
                GROCERY_LISTS_DASHBOARD_ROUTE
            } else {
                GROCERY_LISTS_DASHBOARD_ROUTE
            }
            uiStateFlow.update {
                 MainActivityUiState(
                     startDestinationRoute = startDestinationRoute,
                     defaultListId = defaultListId,
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
}
package com.rendox.grocerygenius

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.checkFirstTimeSync
import com.rendox.grocerygenius.data.user_preferences.UserPreferencesRepository
import com.rendox.grocerygenius.datastore.ChangeListVersionsDataSource
import com.rendox.grocerygenius.feature.dashboard_screen.GROCERY_LISTS_DASHBOARD_ROUTE
import com.rendox.grocerygenius.feature.grocery_list.GROCERY_LIST_CATEGORY_NESTED_NAV_ROUTE_WITH_ARGS
import com.rendox.grocerygenius.feature.onboarding.ONBOARDING_ROUTE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    changeListVersionsDataSource: ChangeListVersionsDataSource,
) : ViewModel() {

    val uiStateFlow = MutableStateFlow<MainActivityUiState?>(null)

    init {
        viewModelScope.launch {
            val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow
            // using only the first value because the nav host start destination
            // should be the same throughout the whole app session (until the app is closed)
            val defaultListId = userPreferencesRepository.getGroceryListIdToOpenOnStartup()
            val changeListVersions = changeListVersionsDataSource.getChangeListVersions()
            val dataHasNotBeenPopulated = listOf(
                changeListVersions.iconVersion,
                changeListVersions.categoryVersion,
                changeListVersions.productVersion,
            ).any { checkFirstTimeSync(localVersion = it) }

            val startDestinationRoute = when {
                dataHasNotBeenPopulated -> ONBOARDING_ROUTE
                defaultListId != null -> GROCERY_LIST_CATEGORY_NESTED_NAV_ROUTE_WITH_ARGS
                else -> GROCERY_LISTS_DASHBOARD_ROUTE
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
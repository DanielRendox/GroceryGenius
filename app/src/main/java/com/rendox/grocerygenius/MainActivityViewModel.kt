package com.rendox.grocerygenius

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.data.user_preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
            // using only the first value because the nav host start destination
            // should be the same throughout the whole app session (until the app is closed)
            val defaultListId = userPreferencesRepository.defaultListId.first()
            uiStateFlow.update {
                MainActivityUiState(defaultListId = defaultListId)
            }
        }
    }
}
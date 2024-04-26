package com.rendox.grocerygenius.feature.grocery_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.data.grocery.GroceryRepository
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import com.rendox.grocerygenius.data.user_preferences.UserPreferencesRepository
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.model.GroceryList
import com.rendox.grocerygenius.network.di.Dispatcher
import com.rendox.grocerygenius.network.di.GroceryGeniusDispatchers
import com.rendox.grocerygenius.ui.components.grocery_list.GroceryGroup
import com.rendox.grocerygenius.ui.helpers.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class GroceryListsSharedViewModel @Inject constructor(
    private val groceryRepository: GroceryRepository,
    private val groceryListRepository: GroceryListRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    @Dispatcher(GroceryGeniusDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    val openedGroceryListIdFlow = MutableStateFlow<String?>(null)

    private val _groceryListsFlow = MutableStateFlow(emptyList<GroceryList>())
    val groceryListsFlow = _groceryListsFlow.asStateFlow()

    var openedGroceryListName by mutableStateOf(TextFieldValue(""))
        private set
    private val openedGroceryListNameFlow = snapshotFlow { openedGroceryListName.text }

    private val _groceryGroupsFlow = MutableStateFlow(emptyList<GroceryGroup>())
    val groceryGroupsFlow = _groceryGroupsFlow.asStateFlow()

    private val _closeGroceryListScreenEvent = MutableStateFlow<UiEvent<Unit>?>(null)
    val closeGroceryListScreenEvent = _closeGroceryListScreenEvent.asStateFlow()

    private val _groceryListEditModeIsEnabledFlow = MutableStateFlow(false)
    val groceryListEditModeIsEnabledFlow = _groceryListEditModeIsEnabledFlow.asStateFlow()

    private val _navigateToGroceryListEvent = MutableStateFlow<UiEvent<Unit>?>(null)
    val navigateToGroceryListEvent = _navigateToGroceryListEvent.asStateFlow()

    private var fetchUpdateListJob: Job? = null

    init {
        println("GroceryListsSharedViewModelDebug init")
        viewModelScope.launch {
            groceryListRepository.getAllGroceryLists().map { groceryLists ->
                groceryLists.sortedBy { it.sortingPriority }
            }.collectLatest { groceryLists ->
                _groceryListsFlow.update { groceryLists }
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.getGroceryListIdToOpenOnStartup()?.let {
                updateOpenedGroceryList(
                    groceryListId = it,
                    navigateToGroceryList = false,
                )
            }
        }
        viewModelScope.launch {
            openedGroceryListIdFlow
                .filterNotNull()
                .flatMapLatest { groceryListId ->
                    groceryRepository.getGroceriesFromList(groceryListId)
                }
                .map { groceries ->
                    groceries
                        .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
                        .groupBy { it.purchased }
                        .toSortedMap()
                        .map { group ->
                            val purchased = group.key
                            val titleId =
                                if (purchased) R.string.purchased_groceries_group_title else null
                            val sortedGroceries = if (purchased) {
                                group.value.sortedByDescending { it.purchasedLastModified }
                            } else {
                                group.value.sortedBy {
                                    it.category?.sortingPriority ?: Int.MAX_VALUE
                                }
                            }
                            GroceryGroup(titleId, sortedGroceries)
                        }
                }
                .flowOn(defaultDispatcher)
                .collectLatest { groceryGroups ->
                    _groceryGroupsFlow.update { groceryGroups }
                }
        }
        viewModelScope.launch {
            _navigateToGroceryListEvent.collect {
                println("GroceryListsSharedViewModelDebug navigateToGroceryListEvent: $it")
            }
        }
        viewModelScope.launch {
            openedGroceryListNameFlow.collect {
                println("UpdateGroceryListNameDebug openedGroceryListNameFlow: $it")
            }
        }
    }

    fun onIntent(intent: GroceryListsUiIntent) = when (intent) {
        is GroceryListsUiIntent.OnGroceryItemClick ->
            toggleItemPurchased(intent.item)

        is GroceryListsUiIntent.UpdateGroceryListName ->
            openedGroceryListName = intent.name

        is GroceryListsUiIntent.OnDeleteGroceryList ->
            deleteGroceryList()

        is GroceryListsUiIntent.OnKeyboardHidden ->
            onKeyboardHidden()

        is GroceryListsUiIntent.OnEditGroceryListToggle ->
            onEditGroceryListToggle(intent.editModeIsEnabled)

        is GroceryListsUiIntent.OnUpdateGroceryLists ->
            updateGroceryLists(intent.groceryLists)

        is GroceryListsUiIntent.OnCreateNewGroceryList ->
            createNewGroceryList()

        is GroceryListsUiIntent.OnOpenGroceryList -> updateOpenedGroceryList(
            groceryListId = intent.id,
            navigateToGroceryList = true,
        )
    }

    private fun toggleItemPurchased(item: Grocery) {
        viewModelScope.launch {
            openedGroceryListIdFlow.value?.let {
                groceryRepository.updatePurchased(
                    productId = item.productId,
                    listId = it,
                    purchased = !item.purchased,
                )
            }
        }
    }

    private fun onKeyboardHidden() {
        if (openedGroceryListName.text.isNotEmpty()) {
            openedGroceryListName = TextFieldValue(openedGroceryListName.text.trim())
            _groceryListEditModeIsEnabledFlow.update { false }
        }
    }

    private fun deleteGroceryList() {
        viewModelScope.launch {
            openedGroceryListIdFlow.value?.let { groceryListId ->
                groceryListRepository.deleteGroceryListById(groceryListId)
                _closeGroceryListScreenEvent.update {
                    object : UiEvent<Unit> {
                        override val data = Unit
                        override fun onConsumed() {
                            _closeGroceryListScreenEvent.update { null }
                        }
                    }
                }
            }
        }
    }

    private fun onEditGroceryListToggle(editModeIsEnabled: Boolean) {
        if (editModeIsEnabled) {
            val nameLength = openedGroceryListName.text.length
            openedGroceryListName = openedGroceryListName.copy(
                selection = TextRange(nameLength, nameLength),
            )
        }
        _groceryListEditModeIsEnabledFlow.update { editModeIsEnabled }
    }

    private fun updateGroceryLists(dashboardItems: List<GroceryList>) {
        viewModelScope.launch {
            val groceryLists = dashboardItems.mapIndexed { index, dashboardItem ->
                GroceryList(
                    id = dashboardItem.id,
                    name = dashboardItem.name,
                    sortingPriority = index.toLong(),
                    numOfGroceries = dashboardItem.numOfGroceries,
                )
            }
            groceryListRepository.upsertGroceryLists(groceryLists)
        }
    }

    private fun createNewGroceryList() {
        viewModelScope.launch {
            val groceryListId = UUID.randomUUID().toString()
            groceryListRepository.insertGroceryList(
                GroceryList(
                    id = groceryListId,
                    name = "",
                )
            )
            updateOpenedGroceryList(
                groceryListId = groceryListId,
                navigateToGroceryList = true,
            )
        }
    }

    private fun updateOpenedGroceryList(
        groceryListId: String,
        navigateToGroceryList: Boolean,
    ) {
        fetchUpdateListJob?.cancel()
        openedGroceryListIdFlow.update { groceryListId }
        fetchUpdateListJob = viewModelScope.launch {
            userPreferencesRepository.updateLastOpenedListId(groceryListId)
            val openedGroceryList = groceryListRepository.getGroceryListById(groceryListId).first()
            if (openedGroceryList != null && openedGroceryList.name.isNotEmpty()) {
                openedGroceryListName = TextFieldValue(openedGroceryList.name)
            } else {
                openedGroceryListName = TextFieldValue("")
                _groceryListEditModeIsEnabledFlow.update { true }
            }

            if (navigateToGroceryList) {
                _navigateToGroceryListEvent.update {
                    object : UiEvent<Unit> {
                        override val data = Unit
                        override fun onConsumed() {
                            _navigateToGroceryListEvent.update { null }
                        }
                    }
                }
            }

            openedGroceryListNameFlow
                .debounce(800)
                .combine(openedGroceryListIdFlow) { listName, groceryListId ->
                    listName to groceryListId
                }
                .collect { (listName, groceryListId) ->
                    groceryListId?.let {
                        groceryListRepository.updateGroceryListName(it, listName.trim())
                    }
                }
        }
    }
}
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
import com.rendox.grocerygenius.data.category.CategoryRepository
import com.rendox.grocerygenius.data.grocery.GroceryRepository
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import com.rendox.grocerygenius.data.product.ProductRepository
import com.rendox.grocerygenius.data.user_preferences.UserPreferencesRepository
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.model.Product
import com.rendox.grocerygenius.network.di.Dispatcher
import com.rendox.grocerygenius.network.di.GroceryGeniusDispatchers
import com.rendox.grocerygenius.ui.components.grocery_list.GroceryGroup
import com.rendox.grocerygenius.ui.helpers.UiEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel(assistedFactory = GroceryListViewModel.Factory::class)
class GroceryListViewModel @AssistedInject constructor(
    @Assisted val openedGroceryListId: String,
    private val groceryRepository: GroceryRepository,
    private val groceryListRepository: GroceryListRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    @Dispatcher(GroceryGeniusDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    var openedGroceryListName by mutableStateOf(TextFieldValue(""))
        private set
    private val openedGroceryListNameFlow = snapshotFlow { openedGroceryListName.text }

    private val _groceryGroupsFlow = MutableStateFlow(emptyList<GroceryGroup>())
    val groceryGroupsFlow = _groceryGroupsFlow.asStateFlow()

    private val _closeGroceryListScreenEvent = MutableStateFlow<UiEvent<Unit>?>(null)
    val closeGroceryListScreenEvent = _closeGroceryListScreenEvent.asStateFlow()

    private val _groceryListEditModeIsEnabledFlow = MutableStateFlow(false)
    val groceryListEditModeIsEnabledFlow = _groceryListEditModeIsEnabledFlow.asStateFlow()

    val categoriesFlow = categoryRepository.getAllCategories()
        .map { categories -> categories.sortedBy { it.sortingPriority } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    private val openedCategoryIdFlow = MutableStateFlow<String?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val openedCategoryFlow = openedCategoryIdFlow
        .mapNotNull { it }
        .flatMapLatest { categoryId ->
            categoryRepository.getCategoryById(categoryId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    private val _openedCategoryGroceriesFlow = MutableStateFlow(emptyList<Grocery>())
    val openedCategoryGroceriesFlow = _openedCategoryGroceriesFlow.asStateFlow()

    private val groceriesInList = groceryRepository.getGroceriesFromList(openedGroceryListId)
    private val categoryProductsFlow = MutableStateFlow(emptyList<Product>())

    private val _navigateToCategoryScreenEvent = MutableStateFlow<UiEvent<Unit>?>(null)
    val navigateToCategoryScreenEvent = _navigateToCategoryScreenEvent.asStateFlow()

    init {
        viewModelScope.launch {
            categoryProductsFlow
                .combine(groceriesInList) { products, groceriesFromList ->
                    products.map { product ->
                        val groceryFromList = groceriesFromList.find { it.productId == product.id }
                        Grocery(
                            productId = product.id,
                            name = product.name,
                            purchased = groceryFromList?.purchased ?: true,
                            icon = product.icon,
                            category = product.category,
                            productIsDefault = product.isDefault,
                        )
                    }
                }.collectLatest { groceries ->
                    _openedCategoryGroceriesFlow.update { groceries }
                }
        }
        viewModelScope.launch {
            userPreferencesRepository.updateLastOpenedListId(openedGroceryListId)
            val openedGroceryList =
                groceryListRepository.getGroceryListById(openedGroceryListId).first()
            if (openedGroceryList != null && openedGroceryList.name.isNotEmpty()) {
                openedGroceryListName = TextFieldValue(openedGroceryList.name)
            } else {
                openedGroceryListName = TextFieldValue("")
                _groceryListEditModeIsEnabledFlow.update { true }
            }

            openedGroceryListNameFlow
                .debounce(800)
                .collect { listName ->
                    groceryListRepository.updateGroceryListName(
                        listId = openedGroceryListId,
                        name = listName.trim(),
                    )
                }
        }
        viewModelScope.launch {
            groceriesInList
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
                                    it.category?.sortingPriority
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

        is GroceryListsUiIntent.OnNavigateToCategoryScreen ->
            onNavigateToCategoryScreen(intent.categoryId)
    }

    fun onCategoryScreenGroceryClick(grocery: Grocery) {
        viewModelScope.launch {
            val groceryIsAlreadyInList =
                groceriesInList.first().any { it.productId == grocery.productId }
            if (groceryIsAlreadyInList) {
                groceryRepository.updatePurchased(
                    productId = grocery.productId,
                    listId = openedGroceryListId,
                    purchased = !grocery.purchased,
                )
            } else {
                groceryRepository.addGroceryToList(
                    productId = grocery.productId,
                    listId = openedGroceryListId,
                    description = grocery.description,
                    purchased = !grocery.purchased,
                )
            }
        }
    }

    private fun toggleItemPurchased(item: Grocery) {
        viewModelScope.launch {
            groceryRepository.updatePurchased(
                productId = item.productId,
                listId = openedGroceryListId,
                purchased = !item.purchased,
            )
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
            groceryListRepository.deleteGroceryListById(openedGroceryListId)
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

    private fun onEditGroceryListToggle(editModeIsEnabled: Boolean) {
        if (editModeIsEnabled) {
            val nameLength = openedGroceryListName.text.length
            openedGroceryListName = openedGroceryListName.copy(
                selection = TextRange(nameLength, nameLength),
            )
        }
        _groceryListEditModeIsEnabledFlow.update { editModeIsEnabled }
    }

    private fun onNavigateToCategoryScreen(categoryId: String) {
        viewModelScope.launch {
            openedCategoryIdFlow.update { categoryId }
            categoryProductsFlow.update { productRepository.getProductsByCategory(categoryId) }
            _navigateToCategoryScreenEvent.update {
                object : UiEvent<Unit> {
                    override val data = Unit
                    override fun onConsumed() {
                        _navigateToCategoryScreenEvent.update { null }
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(openedGroceryListId: String): GroceryListViewModel
    }
}
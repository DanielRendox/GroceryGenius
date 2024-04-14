package com.rendox.grocerygenius.screens.grocery_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.data.grocery.GroceryRepository
import com.rendox.grocerygenius.data.grocery_list.GroceryListRepository
import com.rendox.grocerygenius.data.product.ProductRepository
import com.rendox.grocerygenius.model.CustomProduct
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet.BottomSheetContentType
import com.rendox.grocerygenius.ui.components.grocery_list.GroceryGroup
import com.rendox.grocerygenius.ui.helpers.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class GroceryListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val groceryRepository: GroceryRepository,
    private val groceryListRepository: GroceryListRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    val groceryListId: String = checkNotNull(savedStateHandle[GroceryListIdArg])

    var searchQuery by mutableStateOf("")
        private set
    private val searchQueryFlow = snapshotFlow { searchQuery }

    private val _screenStateFlow = MutableStateFlow(GroceryListScreenState())
    val screenStateFlow = _screenStateFlow.asStateFlow()

    var groceryListName by mutableStateOf(TextFieldValue(""))
        private set
    private val groceryListNameFlow = snapshotFlow { groceryListName.text }

    val groceryGroupsFlow = groceryRepository.getGroceriesFromList(groceryListId)
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
                        group.value.sortedBy { it.category?.sortingPriority ?: Int.MAX_VALUE }
                    }
                    GroceryGroup(titleId, sortedGroceries)
                }
        }
        .stateIn(
            viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5000)
        )

    private val _closeGroceryListScreenEvent = MutableStateFlow<UiEvent<Unit>?>(null)
    val closeGroceryListScreenEvent = _closeGroceryListScreenEvent.asStateFlow()

    init {
        viewModelScope.launch {
            searchQueryFlow.collectLatest { searchQuery ->
                _screenStateFlow.update {
                    it.copy(clearSearchQueryButtonIsShown = searchQuery.isNotEmpty())
                }
                val trimmedSearchInput = searchQuery.trim()
                if (trimmedSearchInput.isNotEmpty()) {
                    updateSearchResults(trimmedSearchInput)
                    _screenStateFlow.update {
                        it.copy(bottomSheetContentType = BottomSheetContentType.SearchResults)
                    }
                } else {
                    _screenStateFlow.update {
                        it.copy(grocerySearchResults = emptyList(), customProduct = null)
                    }
                }
            }
        }
        viewModelScope.launch {
            val groceryList = groceryListRepository.getGroceryListById(groceryListId).first()
            println("NullifyDebug groceryListName is empty ${groceryListName.text.isEmpty()}")
            println("NullifyDebug groceryList from repo: $groceryList")
            if (groceryListName.text.isEmpty()) {
                if (groceryList != null && groceryList.name.isNotEmpty()) {
                    groceryListName = TextFieldValue(groceryList.name)
                } else {
                    _screenStateFlow.update {
                        it.copy(groceryListEditModeIsEnabled = true)
                    }
                }
            }
        }
        viewModelScope.launch {
            _screenStateFlow
                .mapNotNull { it.previousGrocery }
                .flatMapLatest { grocery ->
                    groceryRepository.getGroceryById(
                        productId = grocery.productId,
                        listId = groceryListId,
                    )
                }
                .collectLatest { previousGrocery ->
                    val displayingEditGroceryUi = _screenStateFlow.value.bottomSheetContentType ==
                            BottomSheetContentType.RefineItemOptions
                    val butTheGroceryIsNotInListAnymore = previousGrocery == null
                    if (displayingEditGroceryUi && butTheGroceryIsNotInListAnymore) {
                        _screenStateFlow.update {
                            it.copy(bottomSheetContentType = BottomSheetContentType.Suggestions)
                        }
                    }
                }
        }
        viewModelScope.launch {
            groceryListNameFlow
                .debounce(800)
                .collectLatest { listName ->
                    groceryListRepository.updateGroceryListName(groceryListId, listName.trim())
                }
        }
    }

    fun onIntent(intent: GroceryListScreenIntent) = when (intent) {
        is GroceryListScreenIntent.OnGroceryItemClick ->
            toggleItemPurchased(intent.item)

        is GroceryListScreenIntent.OnSearchQueryChanged ->
            updateSearchQuery(intent.searchInput)

        is GroceryListScreenIntent.OnGrocerySearchResultClick ->
            addOrUpdateGrocery(intent.grocery)

        is GroceryListScreenIntent.OnSearchFieldKeyboardDone ->
            onSearchInputKeyboardDone()

        is GroceryListScreenIntent.OnClearSearchQuery ->
            resetAddGroceryBottomSheet()

        is GroceryListScreenIntent.OnAddGroceryBottomSheetCollapsing ->
            resetAddGroceryBottomSheet()

        is GroceryListScreenIntent.OnCustomProductClick ->
            _screenStateFlow.value.customProduct?.let { addCustomProduct(it) }

        is GroceryListScreenIntent.UpdateGroceryListName ->
            groceryListName = intent.name

        is GroceryListScreenIntent.OnKeyboardHidden ->
            onKeyboardHidden()

        is GroceryListScreenIntent.OnDeleteGroceryList ->
            deleteGroceryList()

        is GroceryListScreenIntent.OnEditGroceryListToggle ->
            onEditGroceryListToggle(intent.editModeIsEnabled)
    }

    private fun toggleItemPurchased(item: Grocery) {
        viewModelScope.launch {
            groceryRepository.updatePurchased(
                productId = item.productId,
                listId = groceryListId,
                purchased = !item.purchased,
            )
        }
    }

    private fun updateSearchQuery(query: String) {
        this.searchQuery = query
        if (query.isEmpty()) {
            _screenStateFlow.update {
                it.copy(bottomSheetContentType = BottomSheetContentType.Suggestions)
            }
        }
    }

    private fun addOrUpdateGrocery(grocery: Grocery) {
        viewModelScope.launch {
            val groceryIsAlreadyInList = groceryGroupsFlow.value
                .flatMap { it.groceries }
                .any { it.productId == grocery.productId }
            if (groceryIsAlreadyInList) {
                toggleItemPurchased(grocery)
            } else {
                groceryRepository.addGroceryToList(
                    productId = grocery.productId,
                    listId = groceryListId,
                    description = grocery.description,
                    purchased = grocery.purchased,
                )
            }
            _screenStateFlow.update {
                it.copy(
                    previousGrocery = grocery,
                    bottomSheetContentType = BottomSheetContentType.RefineItemOptions
                )
            }
        }
        searchQuery = ""
    }

    private fun onSearchInputKeyboardDone() {
        val customProduct = _screenStateFlow.value.customProduct
        if (customProduct != null) {
            addCustomProduct(customProduct)
        } else {
            _screenStateFlow.value.grocerySearchResults.firstOrNull()?.let {
                addOrUpdateGrocery(it)
            }
        }
    }

    private fun resetAddGroceryBottomSheet() {
        searchQuery = ""
        _screenStateFlow.update {
            it.copy(bottomSheetContentType = BottomSheetContentType.Suggestions)
        }
    }

    private fun addCustomProduct(customProduct: CustomProduct) {
        viewModelScope.launch {
            val productId = UUID.randomUUID().toString()
            val purchased = false
            groceryRepository.insertProductAndGrocery(
                productId = productId,
                name = customProduct.name,
                categoryId = customProduct.category?.id,
                groceryListId = groceryListId,
                description = customProduct.description,
            )
            _screenStateFlow.update {
                it.copy(
                    previousGrocery = Grocery(
                        productId = productId,
                        name = customProduct.name,
                        description = customProduct.description,
                        category = customProduct.category,
                        purchased = purchased,
                    ),
                    bottomSheetContentType = BottomSheetContentType.RefineItemOptions,
                )
            }
        }
        searchQuery = ""
    }

    private fun updateSearchResults(searchInput: String) {
        viewModelScope.launch {
            val searchResults = productRepository.getProductsByName("%$searchInput%")
                .sortedWith(
                    compareBy(
                        { !it.name.startsWith(searchInput, ignoreCase = true) },
                        { it.name }
                    )
                )

            val isPerfectMatch =
                searchResults.firstOrNull()?.name.equals(searchInput, ignoreCase = true)

            val newResults = searchResults.map { product ->
                val correspondingGroceryInTheList = groceryGroupsFlow.value
                    .flatMap { it.groceries }
                    .find { it.productId == product.id }
                Grocery(
                    productId = product.id,
                    name = product.name,
                    purchased = correspondingGroceryInTheList?.purchased ?: false,
                    description = correspondingGroceryInTheList?.description,
                    category = product.category,
                    purchasedLastModified = correspondingGroceryInTheList?.purchasedLastModified
                        ?: System.currentTimeMillis(),
                    icon = product.icon,
                )
            }

            _screenStateFlow.update {
                it.copy(
                    customProduct = if (!isPerfectMatch) CustomProduct(
                        name = searchInput
                    ) else null,
                    grocerySearchResults = newResults,
                )
            }
        }
    }

    private fun onKeyboardHidden() {
        if (groceryListName.text.isNotEmpty()) {
            groceryListName = TextFieldValue(groceryListName.text.trim())
            _screenStateFlow.update {
                it.copy(groceryListEditModeIsEnabled = false)
            }
        }
    }

    private fun deleteGroceryList() {
        viewModelScope.launch {
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

    private fun onEditGroceryListToggle(editModeIsEnabled: Boolean) {
        if (editModeIsEnabled) {
            val nameLength = groceryListName.text.length
            groceryListName = groceryListName.copy(
                selection = TextRange(nameLength, nameLength),
            )
        }
        _screenStateFlow.update {
            it.copy(groceryListEditModeIsEnabled = editModeIsEnabled)
        }
    }

}
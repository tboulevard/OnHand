package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.domain.ingredients.GetIngredientsUseCase
import com.tstreet.onhand.core.domain.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.pantry.GetPantryUseCase
import com.tstreet.onhand.core.domain.pantry.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.PantryIngredient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@OptIn(kotlinx.coroutines.FlowPreview::class)
class HomeViewModel @Inject constructor(
    private val getIngredients: Provider<GetIngredientsUseCase>,
    private val addToPantry: Provider<AddToPantryUseCase>,
    private val removeFromPantry: Provider<RemoveFromPantryUseCase>,
    private val getPantry: Provider<GetPantryUseCase>,
) : ViewModel() {

    val pantry = mutableStateListOf<PantryIngredient>()
    // TODO: refactor to work with keys for each item in LazyColumn
    var ingredients = mutableStateListOf<PantryIngredient>()
        private set

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
        refreshPantry()
    }

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText
        .onEach { _isPreSearchDebounce.update { true } }
        .debounce(250L)
        .onEach {
            _isPreSearchDebounce.update { false }
            // Only search and update listed ingredients if we have a valid search query
            if (it.isNotBlank()) {
                _isSearching.update { true }
                ingredients = getIngredients.get().invoke(it).toMutableStateList()
            } else if (ingredients.isNotEmpty()) {
                // Clear the list if search query is blank and we already have listed ingredients
                ingredients.clear()
            }
        }
        // TODO: is there a better operator than combine to appropriately set the backing field?
        .combine(_searchText) { _, _ ->
            _searchText.value
        }
        .onEach {
            _isSearching.update { false }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _searchText.value
        )

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _isSearchBarFocused = MutableStateFlow(false)
    val isSearchBarFocused = _isSearchBarFocused.asStateFlow()

    private val _isPreSearchDebounce = MutableStateFlow(false)
    val isPreSearchDebounce = _isPreSearchDebounce.asStateFlow()

    private val _showErrorDialog = MutableStateFlow(
        ErrorDialogState(shouldDisplay = false)
    )
    val errorDialogState = _showErrorDialog
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _showErrorDialog.value
        )

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
    }

    fun onToggleFromSearch(index: Int) {
        viewModelScope.launch {
            val item = ingredients[index]
            when {
                item.inPantry -> {
                    when (removeFromPantry.get().invoke(item.ingredient).status) {
                        Status.SUCCESS -> {
                            // TODO: cleanup messy double seek to remove one item
                            pantry.remove(pantry.find { it.ingredient.id == item.ingredient.id })
                            ingredients[index] = item.copy(inPantry = false)
                        }
                        Status.ERROR -> {
                            _showErrorDialog.update {
                                ErrorDialogState(
                                    shouldDisplay = true,
                                    message = "Unable to remove item from pantry. Please try again."
                                )
                            }
                        }
                    }
                }
                else -> {
                    when (addToPantry.get().invoke(item.ingredient).status) {
                        Status.SUCCESS -> {
                            // TODO: cleanup messy double seek to remove one item
                            pantry.add(item.copy(inPantry = true))
                            ingredients[index] = item.copy(inPantry = true)
                        }
                        Status.ERROR -> {
                            _showErrorDialog.update {
                                ErrorDialogState(
                                    shouldDisplay = true,
                                    message = "Unable to add item to pantry. Please try again."
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onToggleFromPantry(index: Int) {
        viewModelScope.launch {
            val item = pantry[index]
            // TODO: probably an unnecessary check, but put here to make sure we didn't somehow
            // get an ingredient in the pantry that isn't marked as in the pantry
            if (item.inPantry) {
                when (removeFromPantry.get().invoke(item.ingredient).status) {
                    Status.SUCCESS -> {
                        pantry.removeAt(index)
                    }
                    Status.ERROR -> {
                        _showErrorDialog.update {
                            ErrorDialogState(
                                shouldDisplay = true,
                                message = "Unable to remove item from pantry. Please try again."
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSearchBarFocusChanged(isFocused: Boolean) {
        _isSearchBarFocused.update { isFocused }
    }

    fun dismissErrorDialog() {
        _showErrorDialog.update { ErrorDialogState(shouldDisplay = false) }
    }

    private fun refreshPantry() {
        viewModelScope.launch {
            // TODO: refactor call using .first() once we move pantry to it's own tab
            pantry.addAll(getPantry.get().invoke().first())
        }
    }
}

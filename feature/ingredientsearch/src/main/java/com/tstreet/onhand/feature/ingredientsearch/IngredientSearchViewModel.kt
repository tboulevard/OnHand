package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.domain.AddToPantryUseCase
import com.tstreet.onhand.core.domain.GetIngredientsUseCase
import com.tstreet.onhand.core.domain.GetPantryUseCase
import com.tstreet.onhand.core.domain.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.Ingredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider


//TODO: Encapsulates ingredient search and pantry logic...think about renaming this
@OptIn(kotlinx.coroutines.FlowPreview::class)
class IngredientSearchViewModel @Inject constructor(
    private val getIngredients: Provider<GetIngredientsUseCase>,
    private val addToPantry: Provider<AddToPantryUseCase>,
    private val removeFromPantry: Provider<RemoveFromPantryUseCase>,
    private val getPantry: Provider<GetPantryUseCase>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val pantry = mutableStateListOf<Ingredient>()
    val ingredients = emptyList<Ingredient>().toMutableStateList()

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")

        viewModelScope.launch {
            refreshPantry(shouldClear = false)
        }
    }

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText
        .debounce(500L)
        .onEach {
            // Only search and update listed ingredients if we have a valid search query
            if(it.isNotBlank()) {
                _isSearching.update { true }
                ingredients.clearAndReplaceWith(getIngredients.get().invoke(it))
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
            // TODO: revisit scoping since we're doing database operations behind the scenes
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _searchText.value
        )

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onToggleFromSearch(index: Int) {
        viewModelScope.launch {
            val item = ingredients[index]
            val inPantry = item.inPantry
            ingredients[index] = item.copy(inPantry = !inPantry)

            when (inPantry) {
                true -> {
                    removeFromPantry.get().invoke(item)
                }
                false -> {
                    addToPantry.get().invoke(item)
                }
            }

            // TODO: find element in pantry and only change it instead of reloading the entire pantry
            // TODO: this is also causing issues because we clear the pantry then addall. causes
            // pantry to flash
            refreshPantry()
        }
    }

    fun onTogglefromPantry(index: Int) {
        viewModelScope.launch {
            val item = pantry[index]
            pantry[index] = item.copy(inPantry = false)

            removeFromPantry.get().invoke(item)

            // If the ingredient we toggle from pantry is visible in the search output,
            // we want to update it too
            // TODO: this is causing an issue because we clear THEN update the ingredient search listing
            // TODO: makes the screen look like it's flashing
            ingredients.find {
                item.name == it.name
            }?.let {
                refreshSearchedIngredients()
            }
            refreshPantry()
        }
    }

    private fun refreshSearchedIngredients(shouldClear : Boolean = true) {
        viewModelScope.launch {
            if (shouldClear) {
                ingredients.clear()
            }
            ingredients.addAll(getIngredients.get().invoke(_searchText.value))
        }
    }
    private fun refreshPantry(shouldClear : Boolean = true) {
        viewModelScope.launch {
            if (shouldClear) {
                pantry.clear()
            }
            pantry.addAll(getPantry.get().invoke())
        }
    }

    private fun <T> MutableList<T>.clearAndReplaceWith(newListItems : List<T>) {
        this.clear()
        this.addAll(newListItems)
    }
}

package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.domain.AddToPantryUseCase
import com.tstreet.onhand.core.domain.GetIngredientsUseCase
import com.tstreet.onhand.core.domain.GetPantryUseCase
import com.tstreet.onhand.core.domain.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.PantryIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds


//TODO: Encapsulates ingredient search and pantry logic...think about renaming this
@OptIn(kotlinx.coroutines.FlowPreview::class)
class IngredientSearchViewModel @Inject constructor(
    private val getIngredients: Provider<GetIngredientsUseCase>,
    private val addToPantry: Provider<AddToPantryUseCase>,
    private val removeFromPantry: Provider<RemoveFromPantryUseCase>,
    private val getPantry: Provider<GetPantryUseCase>,
    // TODO: leaving around as an example...
    // Use these dispatchers for things like DB operations that we want to outlive this viewmodel's
    // context.
    // viewModelScope only used for UI related stuff? Research
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val pantry = mutableStateListOf<PantryIngredient>()
    val ingredients = mutableStateListOf<PantryIngredient>()

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
        refreshPantry()
    }

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText
        .debounce(500L)
        .onEach {
            // Only search and update listed ingredients if we have a valid search query
            if (it.isNotBlank()) {
                _isSearching.update { true }
                delay(Random.nextLong(300,600).milliseconds)
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
        println("[OnHand] onSearchTextChange=$text")
        _searchText.value = text
    }

    fun onToggleFromSearch(index: Int) {
        viewModelScope.launch {
            val item = ingredients[index]
            val inPantry = item.inPantry

            when (inPantry) {
                true -> {
                    removeFromPantry.get().invoke(item.ingredient)
                    // TODO: messy double seek to remove one item
                    // TODO: only do this if DB update successful
                    pantry.remove(pantry.find { it.ingredient.id == item.ingredient.id })
                }
                false -> {
                    addToPantry.get().invoke(item.ingredient)
                    // TODO: only do this if DB update successful
                    pantry.add(item.copy(inPantry = true))
                }
            }

            // TODO: Only do this step if DB change is successful in future
            ingredients[index] = item.copy(inPantry = !inPantry)
        }
    }

    fun onToggleFromPantry(index: Int) {
        viewModelScope.launch {
            val item = pantry[index]

            // TODO: probably an unnecessary check, but put here to make sure we didn't somehow
            // get an ingredient in the pantry that isn't marked as such
            if (item.inPantry) {
                removeFromPantry.get().invoke(item.ingredient)
                // TODO: Only do this step if DB change is successful in future
                pantry.removeAt(index)
                // TODO: remove ingredient if shown in ingredients list. messy though...
                ingredients.find { it.ingredient.id == item.ingredient.id }?.let {
                    ingredients[ingredients.indexOf(it)] = it.copy(inPantry = false)
                }
            }
        }
    }

    private fun refreshPantry() {
        viewModelScope.launch {
            // TODO: refactor call using .first() once we move pantry to it's own tab
            pantry.addAll(getPantry.get().invoke().first())
        }
    }

    private fun <T> MutableList<T>.clearAndReplaceWith(newListItems: List<T>) {
        this.clear()
        this.addAll(newListItems)
    }
}

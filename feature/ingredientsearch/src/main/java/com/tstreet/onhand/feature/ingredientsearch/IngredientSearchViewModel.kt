package com.tstreet.onhand.feature.ingredientsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.domain.AddToPantryUseCase
import com.tstreet.onhand.core.domain.GetIngredientsUseCase
import com.tstreet.onhand.core.domain.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.Ingredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

//TODO: Encapsulates ingredient search and pantry logic...think about renaming this
class IngredientSearchViewModel @Inject constructor(
    private val getIngredients: Provider<GetIngredientsUseCase>,
    private val addToPantry: Provider<AddToPantryUseCase>,
    private val removeFromPantry: Provider<RemoveFromPantryUseCase>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _ingredients = MutableStateFlow(listOf<Ingredient>())
    val ingredients : StateFlow<List<Ingredient>> = searchText
        // TODO: use proper operator here...
        .combine(_ingredients) { text, ingredients ->
            if(text.isNotBlank()) {
                getIngredients.get().invoke(text)
            } else {
                // TODO: cleanup
                listOf()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _ingredients.value
        )

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    fun onSearchTextChange(text : String) {
        _searchText.value = text
    }

    fun addIngredientToPantry(ingredient: Ingredient) {
        println("[OnHand] Adding $ingredient to pantry.")
        addToPantry.get().invoke(ingredient)
    }

    fun removeIngredientFromPantry(ingredient: Ingredient) {
        println("[OnHand] Removing $ingredient from pantry.")
        removeFromPantry.get().invoke(ingredient)
    }
}
package com.tstreet.onhand.feature.ingredientsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.domain.AddToPantryUseCase
import com.tstreet.onhand.core.domain.GetIngredientsUseCase
import com.tstreet.onhand.core.domain.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.Ingredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    private val _currentSearchResults = MutableStateFlow(emptyList<Ingredient>())
    val currentSearchResults: StateFlow<List<Ingredient>> = _currentSearchResults.asStateFlow()

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    fun search(prefix: String) {
        viewModelScope.launch(ioDispatcher) {
            _currentSearchResults.value = getIngredients.get().invoke(prefix)
        }
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
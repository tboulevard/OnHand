package com.tstreet.onhand.feature.ingredientsearch

import androidx.lifecycle.ViewModel
import com.tstreet.onhand.core.domain.AddToPantryUseCase
import com.tstreet.onhand.core.domain.GetIngredientsUseCase
import com.tstreet.onhand.core.domain.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject
import javax.inject.Provider

//TODO: Encapsulates ingredient search and pantry logic...think about renaming this
class IngredientSearchViewModel @Inject constructor(
    private val getIngredients: Provider<GetIngredientsUseCase>,
    private val addToPantry: Provider<AddToPantryUseCase>,
    private val removeFromPantry: Provider<RemoveFromPantryUseCase>
) : ViewModel() {

    private var currentSearchResults : List<Ingredient> = listOf()

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    fun search(prefix: String): List<Ingredient> {
        currentSearchResults = getIngredients.get().invoke(prefix)
        return currentSearchResults
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
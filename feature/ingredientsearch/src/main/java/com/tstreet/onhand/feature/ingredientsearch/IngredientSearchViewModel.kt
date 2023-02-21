package com.tstreet.onhand.feature.ingredientsearch

import androidx.lifecycle.ViewModel
import com.tstreet.onhand.core.domain.AddToPantryUseCase
import com.tstreet.onhand.core.domain.GetIngredientsUseCase
import com.tstreet.onhand.core.domain.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject

//TODO: Encapsulates ingredient search and pantry logic...think about renaming this
class IngredientSearchViewModel @Inject constructor(
    private val getIngredients: GetIngredientsUseCase,
    private val addToPantry: AddToPantryUseCase,
    private val removeFromPantry: RemoveFromPantryUseCase
) : ViewModel() {

    private var currentSearchResults : List<Ingredient> = listOf()

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    fun search(prefix: String): List<Ingredient> {
        currentSearchResults = getIngredients(prefix)
        return currentSearchResults
    }

    fun addIngredientToPantry(id : Int) {

    }

    fun removeIngredientFromPantry(id : Int) {

    }
}
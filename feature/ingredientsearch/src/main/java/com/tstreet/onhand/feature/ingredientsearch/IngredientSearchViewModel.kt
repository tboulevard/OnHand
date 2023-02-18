package com.tstreet.onhand.feature.ingredientsearch

import androidx.lifecycle.ViewModel
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject

class IngredientSearchViewModel @Inject constructor(
    private val getIngredients: GetIngredientsUseCase
) : ViewModel() {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

//    val searchPrefix : String = ""
//    val searchResults : List<Ingredient> = emptyList()

    fun search(prefix : String) : List<Ingredient> {
        return getIngredients.invoke(prefix)
    }
}
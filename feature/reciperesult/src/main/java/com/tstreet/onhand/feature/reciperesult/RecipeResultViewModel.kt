package com.tstreet.onhand.feature.reciperesult

import androidx.lifecycle.ViewModel
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject

class RecipeResultViewModel @Inject constructor(
    private val getRecipes: GetRecipesUseCase
) : ViewModel() {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

//    val searchPrefix : String = ""
//    val searchResults : List<Ingredient> = emptyList()

    fun search(prefix: String): List<Ingredient> {
        return getRecipes.invoke(prefix)
    }
}
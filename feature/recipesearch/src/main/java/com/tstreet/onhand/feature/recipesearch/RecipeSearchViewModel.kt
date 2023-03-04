package com.tstreet.onhand.feature.recipesearch

import androidx.lifecycle.ViewModel
import com.tstreet.onhand.core.domain.GetRecipesUseCase
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject

class RecipeSearchViewModel @Inject constructor(
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
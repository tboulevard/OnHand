package com.tstreet.onhand.feature.ingredientsearch

import com.tstreet.onhand.core.domain.GetIngredientsUseCase
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject

class IngredientSearchViewModel @Inject constructor(
    private val getIngredients: GetIngredientsUseCase
) {

//    val searchPrefix : String = ""
//    val searchResults : List<Ingredient> = emptyList()

    fun search(prefix : String) : List<Ingredient> {
        return getIngredients.invoke(prefix)
    }
}
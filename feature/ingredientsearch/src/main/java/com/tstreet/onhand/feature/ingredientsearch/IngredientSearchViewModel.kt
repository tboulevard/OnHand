package com.tstreet.onhand.feature.ingredientsearch

import androidx.lifecycle.ViewModel
import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject

class IngredientSearchViewModel @Inject constructor(
    private val repository: IngredientSearchRepository
) : ViewModel() {

    val searchPrefix : String = ""
    val searchResults : List<Ingredient> = emptyList()

    // TODO: implement...
}
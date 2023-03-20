package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.PantryRepository
import com.tstreet.onhand.core.data.repository.RecipeSearchRepository
import com.tstreet.onhand.core.model.Recipe
import javax.inject.Inject
import javax.inject.Provider

class GetRecipesUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeSearchRepository>,
    private val pantryRepository: Provider<PantryRepository>
) : UseCase() {

    suspend operator fun invoke(): List<Recipe> {
        val allIngredientNames = pantryRepository
            .get()
            .listPantry()
            .map { it.name }

        return recipeRepository
            .get()
            .searchRecipes(allIngredientNames)
    }
}

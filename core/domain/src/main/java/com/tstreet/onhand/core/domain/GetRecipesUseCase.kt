package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.PantryRepository
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class GetRecipesUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
    private val pantryRepository: Provider<PantryRepository>
) : UseCase() {

    operator fun invoke(): Flow<List<Recipe>> {
        val ingredientsInPantryFlow = pantryRepository.get().listPantry().map { ingredientList ->
            ingredientList.map { ingredient ->
                ingredient.name
            }
        }

        return ingredientsInPantryFlow.map {
            // TODO: revisit whether first() is actually what we want here, works for now...
            recipeRepository.get().searchRecipes(it).first()
        }
    }
}

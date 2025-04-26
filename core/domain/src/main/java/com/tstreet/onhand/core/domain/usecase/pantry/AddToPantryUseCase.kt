package com.tstreet.onhand.core.domain.usecase.pantry

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.domain.repository.RecipeRepository
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.model.data.Ingredient
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class AddToPantryUseCase @Inject constructor(
    private val pantryRepository: Provider<PantryRepository>,
    private val recipeRepository: Provider<RecipeRepository>
) : UseCase() {

    suspend operator fun invoke(ingredient: Ingredient): Resource<Unit> {
        val affectedEntities = pantryRepository
            .get()
            .addIngredient(ingredient)

        return when {
            affectedEntities > 0 -> {
                recipeRepository.get().updateSavedRecipesMissingIngredient(ingredient)
                Resource.success(null)
            }
            else -> {
                Resource.error("Unable to add $ingredient to pantry.")
            }
        }
    }
}

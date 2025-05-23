package com.tstreet.onhand.core.domain.usecase.pantry

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.domain.repository.RecipeRepository
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.model.data.Ingredient
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class RemoveFromPantryUseCase @Inject constructor(
    private val pantryRepository: Provider<PantryRepository>,
    private val recipeRepository: Provider<RecipeRepository>,
) : UseCase() {

    suspend operator fun invoke(ingredient: Ingredient): Resource<Unit> {
        val affectedEntities = pantryRepository
            .get()
            .removeIngredient(ingredient)

        return when {
            affectedEntities > 0 -> {
                recipeRepository.get().updateSavedRecipesUsingIngredient(ingredient)
                Resource.success(null)
            }
            else -> {
                Resource.error("Unable to remove $ingredient from pantry.")
            }
        }
    }
}

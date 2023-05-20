package com.tstreet.onhand.core.domain.pantry

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class AddToPantryUseCase @Inject constructor(
    private val pantryRepository: Provider<PantryRepository>,
    private val recipeRepository: Provider<RecipeRepository>,
    private val pantryStateManager: Provider<PantryStateManager>,
) : UseCase() {

    suspend operator fun invoke(ingredient: Ingredient): Resource<Unit> {
        val affectedEntities = pantryRepository
            .get()
            .addIngredient(ingredient)

        return when {
            affectedEntities > 0 -> {
                println("[OnHand] here2")
                pantryStateManager.get().onPantryStateChange()
                recipeRepository.get().updateSavedRecipesMissingIngredient(ingredient)
                Resource.success(null)
            }
            else -> {
                Resource.error("Unable to add $ingredient to pantry.")
            }
        }
    }
}

package com.tstreet.onhand.core.domain.pantry

import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.PantryStateManager
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.UseCase
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

    suspend operator fun invoke(ingredient: Ingredient) : Resource<Unit> {
        val affectedEntities = pantryRepository
            .get()
            .addIngredient(ingredient)

        return when {
            affectedEntities > 0 -> {
                pantryStateManager.get().onPantryStateChange()

                // search recipe cache for ingredient added


                recipeRepository.get().updatedStoredRecipesForIngredient(ingredient)
                // if ingredient missed in recipe ->
                //  remove from missed ingredients
                //  add to used

                Resource.success(null)
            }
            else -> {
                Resource.error("Unable to add $ingredient to pantry.")
            }
        }
    }
}

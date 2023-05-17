package com.tstreet.onhand.core.domain.pantry

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.model.Ingredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
class RemoveFromPantryUseCase @Inject constructor(
    private val pantryRepository: Provider<PantryRepository>,
    private val recipeRepository: Provider<RecipeRepository>,
    private val pantryStateManager: Provider<PantryStateManager>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    suspend operator fun invoke(ingredient: Ingredient): Resource<Unit> {
        return withContext(ioDispatcher) {
            val affectedEntities = pantryRepository
                .get()
                .removeIngredient(ingredient)

            when {
                affectedEntities > 0 -> {
                    pantryStateManager.get().onPantryStateChange()
                    recipeRepository.get().updateSavedRecipesUsingIngredient(ingredient)
                    Resource.success(null)
                }
                else -> {
                    Resource.error("Unable to remove $ingredient from pantry.")
                }
            }
        }
    }
}

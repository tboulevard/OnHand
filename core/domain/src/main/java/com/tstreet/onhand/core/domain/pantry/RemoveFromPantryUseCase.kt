package com.tstreet.onhand.core.domain.pantry

import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.PantryStateManager
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class RemoveFromPantryUseCase @Inject constructor(
    private val repository: Provider<PantryRepository>,
    private val pantryStateManager: Provider<PantryStateManager>,
) : UseCase() {

    suspend operator fun invoke(ingredient: Ingredient): Resource<Unit> {
        val affectedEntities = repository
            .get()
            .removeIngredient(ingredient)

        return when {
            affectedEntities > 0 -> {
                pantryStateManager.get().onPantryStateChange()
                Resource.success(null)
            }
            else -> {
                Resource.error("Unable to remove $ingredient from pantry.")
            }
        }
    }
}

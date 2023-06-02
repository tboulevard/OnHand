package com.tstreet.onhand.core.domain.recipes

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.model.FullRecipe
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Provider

/**
 * Retrieves an individual [FullRecipe] based on its identifier.
 */
@FeatureScope
class GetFullRecipeUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
) : UseCase() {

    operator fun invoke(
        id: Int
    ): Flow<Resource<FullRecipe>> {
        return flow { emit(recipeRepository.get().getFullRecipe(id)) }
    }
}

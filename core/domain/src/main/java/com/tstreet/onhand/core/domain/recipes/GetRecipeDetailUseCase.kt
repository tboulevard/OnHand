package com.tstreet.onhand.core.domain.recipes

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.domain.recipes.SortBy.*
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.RecipeDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

/**
 * Retrieves an individual [Recipe] based on its identifier.
 */
@FeatureScope
class GetRecipeDetailUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator fun invoke(
        id: Int,
        isCustom: Boolean
    ): Flow<Resource<RecipeDetail>> {
        // TODO: revisit, doesn't need to be a flow.
        return flow {
            if (isCustom) {
                emit(recipeRepository.get().getCustomRecipeDetail(id))
            } else {
                emit(recipeRepository.get().getRecipeDetail(id))
            }
        }.flowOn(ioDispatcher)
    }
}

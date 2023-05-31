package com.tstreet.onhand.core.domain.recipes

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.domain.recipes.SortBy.*
import com.tstreet.onhand.core.model.FullRecipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

/**
 * Retrieves an individual [FullRecipe] based on its identifier.
 */
@FeatureScope
class GetFullRecipeUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator fun invoke(
        id: Int,
        isCustom: Boolean
    ): Flow<Resource<FullRecipe>> {
        // TODO: revisit, doesn't need to be a flow.
        // TODO: handle Resources properly...
        return flow {
            if (isCustom) {
                emit(recipeRepository.get().getFullRecipe(id))
            } else {
                val preview = recipeRepository.get().getCachedRecipePreview(id).data!!
                val detail = recipeRepository.get().getRecipeDetail(id).data!!
                emit(Resource.success(data = FullRecipe(preview = preview, detail = detail)))
            }
        }.flowOn(ioDispatcher)
    }
}

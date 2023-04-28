package com.tstreet.onhand.core.domain.recipes

import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.model.RecipeDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class GetRecipeDetailUseCase @Inject constructor(
    private val repository : Provider<RecipeRepository>
) : UseCase() {

    operator fun invoke(id: Int): Flow<RecipeDetail> {
        return repository
            .get()
            .getRecipeDetail(id)
    }
}

package com.tstreet.onhand.core.domain.recipes

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.model.RecipeDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
class GetRecipeDetailUseCase @Inject constructor(
    private val repository: Provider<RecipeRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator fun invoke(id: Int): Flow<Resource<RecipeDetail>> {
        val detail = flow {
            emit(
                repository
                    .get()
                    .getRecipeDetail(id)
            )
        }

        return detail.flowOn(ioDispatcher)
    }
}

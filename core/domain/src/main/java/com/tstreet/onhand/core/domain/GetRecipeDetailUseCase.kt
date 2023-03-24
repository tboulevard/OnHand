package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.RecipeDetailRepository
import com.tstreet.onhand.core.model.RecipeDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Provider

class GetRecipeDetailUseCase @Inject constructor(
    private val repository : Provider<RecipeDetailRepository>
) : UseCase() {

    operator fun invoke(id: Int): Flow<RecipeDetail> {
        return repository
            .get()
            .getRecipeDetail(id)
    }
}

package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.RecipeRepository
import javax.inject.Inject
import javax.inject.Provider

class SaveRecipeUseCase @Inject constructor(
    private val repository: Provider<RecipeRepository>
) : UseCase() {

}

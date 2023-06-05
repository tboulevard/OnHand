package com.tstreet.onhand.core.domain.customrecipe

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import javax.inject.Inject
import javax.inject.Provider

class ValidateCustomRecipeInputUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
) : UseCase() {

    suspend fun recipeWithTitleAlreadyExists(text: String): Boolean {
        return recipeRepository.get().isRecipeSaved(text.hashCode())
    }
}
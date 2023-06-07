package com.tstreet.onhand.core.domain.customrecipe

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import javax.inject.Inject
import javax.inject.Provider

class CustomRecipeInputUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
) : UseCase() {

    /**
     * Given a recipe title, checks if one with that name is already saved.
     */
    suspend fun recipeExists(title: String): Boolean {
        return recipeRepository.get().isRecipeSaved(title)
    }
}
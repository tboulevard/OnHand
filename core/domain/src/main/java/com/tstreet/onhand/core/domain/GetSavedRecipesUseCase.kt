package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.SaveableRecipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class GetSavedRecipesUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>
) : UseCase() {

    operator fun invoke(): Flow<List<SaveableRecipe>> {
        return recipeRepository
            .get()
            .getSavedRecipes()
            .map {
                it.map { recipe ->
                    SaveableRecipe(
                        recipe,
                        isSaved = true // TODO: refactor, for now assume true
                    )
                }
            }
    }
}
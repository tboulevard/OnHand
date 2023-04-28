package com.tstreet.onhand.core.domain.recipes

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.SavedRecipeStateManager
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.model.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
class UnsaveRecipeUseCase @Inject constructor(
    private val repository: Provider<RecipeRepository>,
    private val savedRecipeStateManager: Provider<SavedRecipeStateManager>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    // TODO: Model state using an object (Success/Failure) rather than boolean?
    operator fun invoke(recipe: Recipe): Flow<Boolean> {
        println("[OnHand] UnsaveRecipeUseCase.invoke()")
        val unsaveRecipeFlow = flow {
            repository
                .get()
                .unsaveRecipe(recipe.id)

            // Only invoke state change is the recipe was unsaved successfully
            savedRecipeStateManager.get().onSavedRecipeStateChange()

            emit(true)
        }.catch {
            // TODO: better error handling, and make sure this actually works.
            println("[OnHand] Error removing $recipe from database. Error=${it.message}")
            emit(false)
        }

        return unsaveRecipeFlow.flowOn(ioDispatcher)
    }
}

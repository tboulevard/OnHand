package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.SavedRecipeStateManager
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.model.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
class SaveRecipeUseCase @Inject constructor(
    private val repository: Provider<RecipeRepository>,
    private val savedRecipeStateManager: Provider<SavedRecipeStateManager>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : UseCase() {

    // TODO: Model state using an object (Success/Failure) rather than boolean?
    operator fun invoke(recipe: Recipe): Flow<Boolean> {
        println("[OnHand] SaveRecipeUseCase.invoke()")
        return flow {
            repository.get().saveRecipe(recipe)

            // Only invoke state change is the recipe was saved successfully
            savedRecipeStateManager.get().onSavedRecipeStateChange()

            emit(true)
        }
            .flowOn(ioDispatcher)
            .catch {
                // TODO: better error handling, and make sure this actually works.
                println("[OnHand] Error saving $recipe to database. Error=${it.message}")
                emit(false)
            }
    }
}

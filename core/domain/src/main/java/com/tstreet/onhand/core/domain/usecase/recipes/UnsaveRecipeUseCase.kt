package com.tstreet.onhand.core.domain.usecase.recipes

import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.repository.RecipeRepository
import com.tstreet.onhand.core.model.RecipePreview
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
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    // TODO: Model state using an object (Success/Failure) rather than boolean?
    operator fun invoke(recipePreview: RecipePreview): Flow<Boolean> {
        Log.d("[OnHand]", "UnsaveRecipeUseCase.invoke()")
        val unsaveRecipeFlow = flow {
            repository
                .get()
                .unsaveRecipe(recipePreview.id)

            emit(true)
        }.catch {
            // TODO: better error handling, and make sure this actually works.
            Log.d("[OnHand]", "Error removing $recipePreview from database. Error=${it.message}")
            emit(false)
        }

        return unsaveRecipeFlow.flowOn(ioDispatcher)
    }
}

package com.tstreet.onhand.core.domain.recipes

import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.model.RecipePreview
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
class SaveRecipeUseCase @Inject constructor(
    private val repository: Provider<RecipeRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : UseCase() {

    // TODO: Model state using an object (Success/Failure) rather than boolean?
    operator fun invoke(recipePreview: RecipePreview): Flow<Boolean> {
        Log.d("[OnHand]", "SaveRecipeUseCase.invoke()")
        return flow {
            repository.get().saveRecipePreview(recipePreview)
            emit(true)
        }
            .flowOn(ioDispatcher)
            .catch {
                // TODO: better error handling, and make sure this actually works.
                Log.d("[OnHand]", "Error saving $recipePreview to database. Error=${it.message}")
                emit(false)
            }
    }
}

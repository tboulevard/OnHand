package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.CommonModule
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.model.SaveableRecipe
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
    @Named(CommonModule.IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    // TODO: Model state using an object (Success/Failure) rather than boolean?
    operator fun invoke(saveableRecipe: SaveableRecipe): Flow<Boolean> {
        val unsaveRecipeFlow = flow {
            repository
                .get()
                .unsaveRecipe(saveableRecipe.recipe.id)
            emit(true)
        }.catch {
            // TODO: better error handling, and make sure this actually works.
            println("[OnHand] Error removing $saveableRecipe from database. Error=${it.message}")
            emit(false)
        }

        return unsaveRecipeFlow.flowOn(ioDispatcher)
    }
}

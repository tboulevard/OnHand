package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.CommonModule
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.SaveableRecipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.invoke
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
class SaveRecipeUseCase @Inject constructor(
    private val repository: Provider<RecipeRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : UseCase() {

    // TODO: Propagate boolean (as return value here) and catch any DB exception to prevent crashes?
    suspend operator fun invoke(saveableRecipe: SaveableRecipe) {
        println("[OnHand] SaveRecipeUseCase invoke: $saveableRecipe")
        when (saveableRecipe.isSaved) {
            true -> {
                repository.get().unSaveRecipe(saveableRecipe.recipe.id)
            }
            false -> {
                repository
                    .get()
                    .getRecipeDetail(saveableRecipe.recipe.id)
                    .flowOn(ioDispatcher)
                    .collect {
                        repository.get().saveRecipe(it)
                    }
            }
        }
    }
}

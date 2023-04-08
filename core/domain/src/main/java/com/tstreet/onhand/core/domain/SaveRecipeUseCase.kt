package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.model.CompositeRecipe
import com.tstreet.onhand.core.model.SaveableRecipe
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
    operator fun invoke(saveableRecipe: SaveableRecipe): Flow<Boolean> {
        return repository
            .get()
            .getRecipeDetail(saveableRecipe.recipe.id)
            .map { recipeDetail ->
                repository.get().saveRecipe(
                    CompositeRecipe(
                        id = saveableRecipe.recipe.id,
                        title = saveableRecipe.recipe.title,
                        image = saveableRecipe.recipe.image,
                        imageType = saveableRecipe.recipe.imageType,
                        missedIngredients = saveableRecipe.recipe.missedIngredients,
                        missedIngredientCount = saveableRecipe.recipe.missedIngredientCount,
                        usedIngredients = saveableRecipe.recipe.usedIngredients,
                        usedIngredientCount = saveableRecipe.recipe.usedIngredientCount,
                        likes = saveableRecipe.recipe.likes,
                        sourceUrl = recipeDetail.sourceUrl
                    )
                )
                true
            }
            .flowOn(ioDispatcher)
            .catch {
                // TODO: better error handling, and make sure this actually works.
                println("[OnHand] Error saving $saveableRecipe to database. Error=${it.message}")
                emit(false)
            }
    }
}

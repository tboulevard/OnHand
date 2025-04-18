package com.tstreet.onhand.core.domain.recipes

import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.model.SaveableRecipePreview
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
class GetSavedRecipesUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator fun invoke(): Flow<List<SaveableRecipePreview>> {
        Log.d("[OnHand]", "GetSavedRecipesUseCase.invoke()")
        val savedRecipeFlow = recipeRepository
            .get()
            .getSavedRecipes()

        return savedRecipeFlow.flowOn(ioDispatcher)
    }
}

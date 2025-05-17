package com.tstreet.onhand.core.domain.usecase.recipes

import android.util.Log
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.repository.RecipeRepository
import com.tstreet.onhand.core.model.RecipePreview
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class UnsaveRecipeUseCase @Inject constructor(
    private val repository: Provider<RecipeRepository>
) : UseCase() {

    suspend operator fun invoke(recipe: RecipePreview): Resource<Unit> {
        Log.d("[OnHand]", "SaveRecipeUseCase.invoke()")
        val result = repository.get().unsaveRecipe(recipe.id)

        return when (result.status) {
            Status.SUCCESS -> {
                Resource.success(Unit)
            }

            Status.ERROR -> {
                Log.d("[OnHand]", "Error unsaving recipe: ${result.message}")
                Resource.error(result.message ?: "Error saving recipe")
            }
        }
    }
}

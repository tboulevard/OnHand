package com.tstreet.onhand.core.domain.customrecipe

import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.model.PartialRecipe
import com.tstreet.onhand.core.model.Recipe
import javax.inject.Inject
import javax.inject.Provider
import kotlin.random.Random

class AddRecipeUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>
) : UseCase() {

    suspend operator fun invoke(partialRecipe: PartialRecipe): Resource<Unit> {
        println("[OnHand] AddRecipeUseCase - Adding recipe=${partialRecipe.recipeTitle}")

        recipeRepository.get().saveRecipe(
            Recipe(
                id = Random(10000).nextInt(),
                title = partialRecipe.recipeTitle,
                image = partialRecipe.recipeImage,
                imageType = partialRecipe.recipeImageType,
                usedIngredients = emptyList(),
                usedIngredientCount = 1,
                missedIngredients = emptyList(),
                missedIngredientCount = 2,
                instructions = partialRecipe.recipeInstructions,
                likes = -1,
            )
        )

        return Resource.success(null)
    }
}
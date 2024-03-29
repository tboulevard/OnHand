package com.tstreet.onhand.core.domain.customrecipe

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status.*
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.model.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class AddRecipeUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
    private val pantryRepository: Provider<PantryRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    /**
     * @param customRecipeInput - Input received from the user, collected on custom recipe creation.
     *
     * @return A flow wrapped [Resource] containing the id of the recipe created.
     */
    operator fun invoke(customRecipeInput: CustomRecipeInput): Flow<Resource<Int>> {
        println("[OnHand] Adding recipe with input=${customRecipeInput.recipeTitle}")
        return pantryRepository.get().listPantry().map {
            when (it.status) {
                SUCCESS -> {
                    val usedIngredients = it.data?.let { pantryIngredients ->
                        calculateUsedIngredients(
                            pantryIngredients = pantryIngredients,
                            customRecipeIngredients = customRecipeInput.ingredients
                        )
                    } ?: emptyList()
                    val missedIngredients = it.data?.let { pantryIngredients ->
                        calculateMissedIngredients(
                            pantryIngredients = pantryIngredients,
                            customRecipeIngredients = customRecipeInput.ingredients
                        )
                    } ?: emptyList()

                    val result: SaveRecipeResult = recipeRepository.get().saveFullRecipe(
                        fullRecipe = FullRecipe(
                            preview = RecipePreview(
                                // TODO: for now, we just assign id based on hash of title -
                                //  potentially look into a more stable approach in the future.
                                id = customRecipeInput.recipeTitle.hashCode(),
                                title = customRecipeInput.recipeTitle,
                                image = customRecipeInput.recipeImage,
                                imageType = customRecipeInput.recipeImageType,
                                usedIngredients = usedIngredients,
                                usedIngredientCount = usedIngredients.size,
                                missedIngredients = missedIngredients,
                                missedIngredientCount = missedIngredients.size,
                                isCustom = true,
                                // TODO: revisit - for now custom recipes just won't have likes
                                likes = -1,
                            ),
                            detail = RecipeDetail(
                                instructions = customRecipeInput.instructions
                            )
                        )
                    )

                    when (result.status) {
                        Status.SUCCESS -> {
                            Resource.success(result.recipeId)
                        }
                        Status.NAME_CONFLICT -> {
                            Resource.error(
                                "Unable to save recipe: One already exists with that name. " +
                                        "Please enter a different name."
                            )
                        }
                        Status.UNKNOWN_ERROR -> Resource.error(
                            "Unable to save recipe: ${result.message}"
                        )
                    }
                }
                ERROR -> {
                    // TODO: revisit when saveRecipe() API is updated
                    Resource.error(msg = it.message.toString())
                }
            }
            // So missing/used ingredient calculation doesn't happen in ViewModel coroutine scope...
        }.flowOn(ioDispatcher)
    }

    /**
     * Returns ingredients that are required by the custom recipe but not in the pantry.
     */
    private fun calculateMissedIngredients(
        pantryIngredients: List<PantryIngredient>,
        customRecipeIngredients: List<RecipeIngredient>
    ): List<RecipeIngredient> {
        val newList = mutableListOf<RecipeIngredient>()
        customRecipeIngredients.filterTo(destination = newList) { customRecipeIngredient ->
            // Filter the ingredient if we find it in the pantry (not missing)
            pantryIngredients.find { customRecipeIngredient.ingredient.name == it.ingredient.name } == null
        }
        return newList
    }

    /**
     * Returns ingredients that are required by the custom recipe and also in the pantry.
     */
    private fun calculateUsedIngredients(
        pantryIngredients: List<PantryIngredient>,
        customRecipeIngredients: List<RecipeIngredient>
    ): List<RecipeIngredient> {
        val newList = mutableListOf<RecipeIngredient>()
        customRecipeIngredients.filterTo(destination = newList) { customRecipeIngredient ->
            // Filter the ingredient if we do not find it in the pantry (not used)
            pantryIngredients.find { customRecipeIngredient.ingredient.name == it.ingredient.name } != null
        }
        return newList
    }
}

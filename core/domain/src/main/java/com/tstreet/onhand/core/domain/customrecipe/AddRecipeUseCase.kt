package com.tstreet.onhand.core.domain.customrecipe

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status.*
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.model.PantryIngredient
import com.tstreet.onhand.core.model.PartialRecipe
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.RecipeIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

    operator fun invoke(partialRecipe: PartialRecipe): Flow<Resource<Unit>> {
        println("[OnHand] AddRecipeUseCase - Adding recipe=${partialRecipe.recipeTitle}, ingredients=${partialRecipe.ingredients}")

        return pantryRepository.get().listPantry().map {
            when (it.status) {
                SUCCESS -> {
                    val usedIngredients = it.data?.let { pantryIngredients ->
                        calculateUsedIngredients(
                            pantryIngredients = pantryIngredients,
                            customRecipeIngredients = partialRecipe.ingredients
                        )
                    } ?: emptyList()
                    val missedIngredients = it.data?.let { pantryIngredients ->
                        calculateMissedIngredients(
                            pantryIngredients = pantryIngredients,
                            customRecipeIngredients = partialRecipe.ingredients
                        )
                    } ?: emptyList()

                    recipeRepository.get().saveRecipe(
                        Recipe(
                            // TODO: for now, we just assign id based on hash of title - potentially
                            //  look into a more stable approach in the future.
                            id = partialRecipe.recipeTitle.hashCode(),
                            title = partialRecipe.recipeTitle,
                            image = partialRecipe.recipeImage,
                            imageType = partialRecipe.recipeImageType,
                            usedIngredients = usedIngredients,
                            usedIngredientCount = usedIngredients.size,
                            missedIngredients = missedIngredients,
                            missedIngredientCount = missedIngredients.size,
                            instructions = partialRecipe.recipeInstructions,
                            // TODO: revisit - for now custom recipes just won't have likes
                            likes = -1,
                        )
                    )
                    Resource.success(null)
                }
                ERROR -> {
                    // TODO: revisit when saveRecipe() API is updated
                    Resource.error(msg = it.message.toString())
                }
            }
        }.catch {
            Resource.error<Nothing>(msg = it.message.toString())
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
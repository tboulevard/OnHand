package com.tstreet.onhand.core.domain.usecase.customrecipe

import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.domain.repository.RecipeRepository
import com.tstreet.onhand.core.model.*
import com.tstreet.onhand.core.model.data.Ingredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
        Log.d("[OnHand]", "Adding recipe with input=${customRecipeInput.recipeTitle}")
        return flowOf(Resource.success(0))
    }

    /**
     * Returns ingredients that are required by the custom recipe but not in the pantry.
     */
    private fun calculateMissedIngredients(
        pantryIngredients: List<Ingredient>,
        customRecipeIngredients: List<Ingredient>
    ): List<Ingredient> {
        val newList = mutableListOf<Ingredient>()
        customRecipeIngredients.filterTo(destination = newList) { customRecipeIngredient ->
            // Filter the ingredient if we find it in the pantry (not missing)
            pantryIngredients.find { customRecipeIngredient.name == it.name } == null
        }
        return newList
    }

    /**
     * Returns ingredients that are required by the custom recipe and also in the pantry.
     */
    private fun calculateUsedIngredients(
        pantryIngredients: List<Ingredient>,
        customRecipeIngredients: List<Ingredient>
    ): List<Ingredient> {
        val newList = mutableListOf<Ingredient>()
        customRecipeIngredients.filterTo(destination = newList) { customRecipeIngredient ->
            // Filter the ingredient if we do not find it in the pantry (not used)
            pantryIngredients.find { customRecipeIngredient.name == it.name } != null
        }
        return newList
    }
}

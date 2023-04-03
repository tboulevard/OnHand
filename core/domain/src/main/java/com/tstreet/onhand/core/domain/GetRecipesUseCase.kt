package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.PantryRepository
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.domain.SortBy.*
import com.tstreet.onhand.core.model.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
class GetRecipesUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
    private val pantryRepository: Provider<PantryRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : UseCase() {

    operator fun invoke(sortBy: SortBy = DEFAULT_SORTING): Flow<List<Recipe>> {
        println("[OnHand] GetRecipesUseCase.invoke($sortBy)")
        val recipes = getPantryIngredients()
            .map { ingredients ->
                val recipes = recipeRepository.get().searchRecipes(ingredients)
                when (sortBy) {
                    POPULARITY -> recipes.sortedByDescending { it.likes }
                    MISSING_INGREDIENTS -> recipes.sortedBy { it.missedIngredientCount }
                }
            }

        return recipes.flowOn(ioDispatcher)
    }

    private fun getPantryIngredients(): Flow<List<String>> {
        return pantryRepository
            .get()
            .listPantry()
            .map { ingredientList ->
                ingredientList
                    .map { ingredient ->
                        ingredient.name
                    }
            }
    }
}

enum class SortBy {
    POPULARITY,
    MISSING_INGREDIENTS
}

val DEFAULT_SORTING = POPULARITY

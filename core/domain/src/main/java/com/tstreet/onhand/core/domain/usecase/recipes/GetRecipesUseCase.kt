package com.tstreet.onhand.core.domain.usecase.recipes

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.domain.repository.RecipeRepository
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.RecipePreviewWithSaveState
import com.tstreet.onhand.core.model.domain.RecipeSearchResult
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Provider

/**
 * Retrieves recipes based on contents of pantry.
 */
@FeatureScope
class GetRecipesUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
    private val pantryRepository: Provider<PantryRepository>
) : UseCase() {

    operator fun invoke(sortBy: SortBy = DEFAULT_SORTING): Flow<RecipeSearchResult> {
        val recipes = getPantryIngredients().mapPantryToSortedRecipes(sortBy)

        return recipes
            .catch {
                emit(RecipeSearchResult.Error)
            }
            .onStart {
                emit(RecipeSearchResult.Loading)
            }
    }

    private fun Flow<List<Ingredient>>.mapPantryToSortedRecipes(sortBy: SortBy): Flow<RecipeSearchResult> {
        return map { ingredients ->
            if (ingredients.isNotEmpty()) {
                val recipes = getRecipesForPantry(ingredients)
                val sortedRecipes = when (sortBy) {
                    SortBy.POPULARITY -> recipes.sortedByDescending { it.preview.likes }
                    SortBy.MISSING_INGREDIENTS -> recipes.sortedBy { it.preview.missedIngredientCount }
                }

                RecipeSearchResult.Success(sortedRecipes)
            } else {
                // TODO: Return some default list of recipes (most popular?)
                RecipeSearchResult.Success(emptyList())
            }
        }
    }

    private suspend fun getRecipesForPantry(pantryIngredients: List<Ingredient>): List<RecipePreviewWithSaveState> {
        val recipeResource = recipeRepository.get().findRecipes(
            fetchStrategy = FetchStrategy.NETWORK, ingredients = pantryIngredients.map { it.name }
        )

        return recipeResource.data?.let {
            it.map { recipe ->
                // TODO: make this a bulk operation -- many segmented DB reads this way
                //  Also - this is retriggered when we sort for each element in list; unnecessary
                //  if list contents haven't changed. Look into caching the results to re-use
                //  specifically for sorting
                val isRecipeSaved = recipeRepository.get().isRecipeSaved(recipe.id)
                RecipePreviewWithSaveState(
                    preview = recipe, isSaved = isRecipeSaved
                )
            }
        } ?: emptyList()
    }

    private fun getPantryIngredients(): Flow<List<Ingredient>> {
        return flow {
            emit(pantryRepository.get().listPantry())
        }
    }
}

enum class SortBy {
    POPULARITY, MISSING_INGREDIENTS
}

val DEFAULT_SORTING = SortBy.POPULARITY

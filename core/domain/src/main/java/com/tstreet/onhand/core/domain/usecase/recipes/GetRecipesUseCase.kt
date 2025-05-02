package com.tstreet.onhand.core.domain.usecase.recipes

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.domain.repository.RecipeRepository
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.model.ShoppingListIngredient
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.RecipePreviewWithSaveState
import com.tstreet.onhand.core.model.domain.RecipeSearchResult
import com.tstreet.onhand.core.model.domain.SavedRecipesResult
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Provider

/**
 * Retrieves recipes and contextualizes information about the pantry and shopping list.
 */
@FeatureScope
class GetRecipesUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
    private val pantryRepository: Provider<PantryRepository>,
    private val shoppingListRepository: Provider<ShoppingListRepository>
) : UseCase() {

    operator fun invoke(sortBy: SortBy = DEFAULT_SORTING): Flow<RecipeSearchResult> {
        val recipes =
            getPantryIngredients().mapPantryToSortedRecipes(getShoppingListIngredients(), sortBy)

        return recipes
            .catch {
                emit(RecipeSearchResult.Error)
            }
            .onStart {
                emit(RecipeSearchResult.Loading)
            }
    }

    fun getSavedRecipes(): Flow<SavedRecipesResult> {
        return recipeRepository.get().getSavedRecipes()
            .mapToSavedRecipesResult()
            .onStart {
                emit(SavedRecipesResult.Loading)
            }.catch {
                emit(SavedRecipesResult.Error)
            }
    }

    private fun Flow<List<RecipePreviewWithSaveState>>.mapToSavedRecipesResult(): Flow<SavedRecipesResult> {
        return map { SavedRecipesResult.Success(it) }
    }

    private fun Flow<List<Ingredient>>.mapPantryToSortedRecipes(
        shoppingListIngredientsFlow: Flow<Resource<List<ShoppingListIngredient>>>,
        sortBy: SortBy
    ): Flow<RecipeSearchResult> {
        return combine(shoppingListIngredientsFlow) { pantryIngredients, shoppingListIngredients ->
            if (pantryIngredients.isNotEmpty()) {
                val recipes = getRecipesForPantry(pantryIngredients, shoppingListIngredients)
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

    private suspend fun getRecipesForPantry(
        pantryIngredients: List<Ingredient>,
        shoppingListIngredients: Resource<List<ShoppingListIngredient>>
    ): List<RecipePreviewWithSaveState> {
        val recipeResource = recipeRepository.get().findRecipes(
            fetchStrategy = FetchStrategy.NETWORK, ingredients = pantryIngredients.map { it.name }
        )

        val shoppingListIngredientNames =
            shoppingListIngredients.data?.map { it.name } ?: emptyList()

        return recipeResource.data?.let {
            it.map { recipe ->
                val itemsMissingButInShoppingList =
                    recipe.missedIngredients.filter { shoppingListIngredientNames.contains(it.ingredient.name) }
                // TODO: make this a bulk operation -- many segmented DB reads this way
                //  Also - this is retriggered when we sort for each element in list; unnecessary
                //  if list contents haven't changed. Look into caching the results to re-use
                //  specifically for sorting
                val isRecipeSaved = recipeRepository.get().isRecipeSaved(recipe.id)
                RecipePreviewWithSaveState(
                    preview = recipe,
                    isSaved = isRecipeSaved,
                    ingredientsMissingButInShoppingList = itemsMissingButInShoppingList.map { it.ingredient }
                )
            }
        } ?: emptyList()
    }

    private fun getPantryIngredients(): Flow<List<Ingredient>> {
        return flow {
            emit(pantryRepository.get().listPantry())
        }
    }

    private fun getShoppingListIngredients(): Flow<Resource<List<ShoppingListIngredient>>> {
        return shoppingListRepository.get().getShoppingList()
    }
}

enum class SortBy {
    POPULARITY, MISSING_INGREDIENTS
}

val DEFAULT_SORTING = SortBy.POPULARITY

package com.tstreet.onhand.core.domain.usecase.recipes

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.domain.repository.RecipeRepository
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.model.data.ShoppingListIngredient
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
        return mapSortedRecipes(
            getPantryIngredients(),
            getShoppingListIngredients(),
            sortBy
        ).catch {
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

    private fun mapSortedRecipes(
        pantryIngredientsFlow: Flow<List<Ingredient>>,
        shoppingListIngredientsFlow: Flow<Resource<List<ShoppingListIngredient>>>,
        sortBy: SortBy
    ): Flow<RecipeSearchResult> {
        return pantryIngredientsFlow.combine(shoppingListIngredientsFlow) { pantryIngredients, shoppingListIngredients ->
            if (pantryIngredients.isNotEmpty()) {
                // Get recipes with metadata on missed ingredients, save state, etc.
                val recipes =
                    getRecipesForPantryAndShoppingList(pantryIngredients, shoppingListIngredients)

                // Sort
                val sortedRecipes = when (sortBy) {
                    SortBy.POPULARITY -> recipes.sortedByDescending { it.preview.likes }
                    SortBy.MISSING_INGREDIENTS -> recipes.sortedBy { it.preview.missedIngredientCount }
                }

                RecipeSearchResult.Success(sortedRecipes)
            } else {
                // Pantry is empty
                // TODO: Return some default list of recipes (most popular?)
                RecipeSearchResult.Success(emptyList())
            }
        }
    }

    /**
     * Takes pantry ingredients and combines them with the shopping list to return a list of recipes
     * with metadata about how many ingredients are missing and if they are in the shopping list
     * and/or pantry.
     */
    private suspend fun getRecipesForPantryAndShoppingList(
        pantryIngredients: List<Ingredient>,
        shoppingListIngredients: Resource<List<ShoppingListIngredient>>
    ): List<RecipePreviewWithSaveState> {

        // Recipes based on pantry
        val recipeResource = recipeRepository.get().findRecipes(
            fetchStrategy = FetchStrategy.NETWORK,
            ingredients = pantryIngredients
        )

        // Ingredients in shopping list
        val shoppingListIngredientNames =
            shoppingListIngredients.data?.map { it.name } ?: emptyList()

        return when (recipeResource.status) {
            Status.SUCCESS -> {
                val recipes = recipeResource.data ?: emptyList()
                return recipes.map { recipe ->
                    val missedIngredientsInShoppingList =
                        recipe.missedIngredients.filter { shoppingListIngredientNames.contains(it.name) }
                    RecipePreviewWithSaveState(
                        preview = recipe,
                        // TODO: make this a bulk operation -- many segmented DB reads this way
                        //  Also - this is retriggered when we sort for each element in list; unnecessary
                        //  if list contents haven't changed. Look into caching the results to re-use
                        //  specifically for sorting
                        isSaved = recipeRepository.get().isRecipeSaved(recipe.id),
                        ingredientsMissingButInShoppingList = missedIngredientsInShoppingList
                    )
                }
            }

            Status.ERROR -> {
                return emptyList()
            }
        }
    }

    private fun getPantryIngredients(): Flow<List<Ingredient>> {
        return flow {
            emit(pantryRepository.get().listPantry())
        }
    }

    private fun getShoppingListIngredients(): Flow<Resource<List<ShoppingListIngredient>>> {
        return flow {
            emit(shoppingListRepository.get().getShoppingList())
        }
    }
}

enum class SortBy {
    POPULARITY, MISSING_INGREDIENTS
}

val DEFAULT_SORTING = SortBy.POPULARITY

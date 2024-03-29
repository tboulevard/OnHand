package com.tstreet.onhand.core.domain.recipes

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.domain.recipes.SortBy.*
import com.tstreet.onhand.core.model.SaveableRecipePreview
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

// TODO: for some reason this scope isn't needed to bind use case to view lifecycle...is it
//  because [RecipeResultComponent] already specifies this? Either way, keeping
//  here to be pedantic...
// TODO: Also - Is it correct to have this annotation at the class or module level? For all
//  use cases, class level appears to work while module level does not
/**
 * Retrieves recipes based on contents of pantry.
 */
@FeatureScope
class GetRecipesUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
    private val pantryRepository: Provider<PantryRepository>,
    private val pantryStateManager: Provider<PantryStateManager>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator fun invoke(sortBy: SortBy = DEFAULT_SORTING): Flow<Resource<List<SaveableRecipePreview>>> {
        // TODO: An error retrieving pantry ingredients makes us transmit empty list to ViewModel -
        //  we may want to explicitly propagate the error up later
        val recipes = getPantryIngredients().map { ingredients ->
            if (ingredients.isNotEmpty()) {
                val recipes = findSaveableRecipes(ingredients)
                val sortedRecipes = when (sortBy) {
                    POPULARITY -> recipes.data?.sortedByDescending { it.recipePreview.likes }
                    MISSING_INGREDIENTS -> recipes.data?.sortedBy { it.recipePreview.missedIngredientCount }
                }
                when (recipes.status) {
                    SUCCESS -> {
                        // TODO: pantry reset logic messed up, look into before merging...
                        pantryStateManager.get().onResetPantryState()
                        Resource.success(data = sortedRecipes)
                    }
                    ERROR -> {
                        println(
                            "[OnHand] Pantry state not reset because there was an error " + "retrieving recipes."
                        )
                        Resource.error(
                            msg = recipes.message.toString(), data = sortedRecipes
                        )
                    }
                }
            } else {
                Resource.success(emptyList())
            }
        }

        return recipes.flowOn(ioDispatcher)
    }

    private suspend fun findSaveableRecipes(ingredientNames: List<String>): Resource<List<SaveableRecipePreview>> {
        val recipeResource = recipeRepository.get().findRecipes(
            fetchStrategy = getFetchStrategy(), ingredients = ingredientNames
        )

        val saveableRecipeResource = recipeResource.data?.let {
            it.map { recipe ->
                // TODO: make this a bulk operation -- many segmented DB reads this way
                //  Also - this is retriggered when we sort for each element in list; unnecessary
                //  if list contents haven't changed. Look into caching the results to re-use
                //  specifically for sorting
                val isRecipeSaved = recipeRepository.get().isRecipeSaved(recipe.id)
                SaveableRecipePreview(
                    recipePreview = recipe, isSaved = isRecipeSaved
                )
            }
        }

        return when (recipeResource.status) {
            SUCCESS -> {
                Resource.success(data = saveableRecipeResource)
            }
            ERROR -> {
                Resource.error(
                    msg = recipeResource.message.toString(), data = saveableRecipeResource
                )
            }
        }
    }

    private fun getPantryIngredients(): Flow<List<String>> {
        return pantryRepository.get().listPantry().map {
            when (it.status) {
                SUCCESS -> {
                    it.data?.map { item -> item.ingredient.name } ?: emptyList()
                }
                ERROR -> {
                    println(
                        "[OnHand] There was an error getting the pantry ingredients" + "for recipes."
                    )
                    emptyList()
                }
            }
        }
    }

    private fun getFetchStrategy() =
    // TODO: Think about adding centralized network check logic or similar to prevent
        //  network error dialog from showing every time sort order is changed
        when (pantryStateManager.get().hasPantryStateChanged()) {
            true -> FetchStrategy.NETWORK
            false -> FetchStrategy.DATABASE
        }
}

enum class SortBy {
    POPULARITY, MISSING_INGREDIENTS
}

val DEFAULT_SORTING = POPULARITY

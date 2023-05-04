package com.tstreet.onhand.core.domain.recipes

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.domain.recipes.SortBy.*
import com.tstreet.onhand.core.model.SaveableRecipe
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
@FeatureScope
class GetRecipesUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
    private val pantryRepository: Provider<PantryRepository>,
    private val pantryStateManager: Provider<PantryStateManager>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator fun invoke(sortBy: SortBy = DEFAULT_SORTING): Flow<Resource<List<SaveableRecipe>>> {
        val recipes = getPantryIngredients()
            .map { ingredients ->
                if (ingredients.isNotEmpty()) {
                    val recipes = findSaveableRecipes(ingredients)
                    when (recipes.status) {
                        SUCCESS -> {
                            val result = Resource.success(
                                data = when (sortBy) {
                                    POPULARITY ->
                                        recipes.data?.sortedByDescending { it.recipe.likes }
                                    MISSING_INGREDIENTS ->
                                        recipes.data?.sortedBy { it.recipe.missedIngredientCount }
                                }
                            )
                            // TODO: pantry reset logic messed up, look into before merging...
                            pantryStateManager.get().onResetPantryState()
                            result
                        }
                        ERROR -> {
                            println(
                                "[OnHand] Pantry state not reset because there was an error " +
                                        "retrieving recipes."
                            )
                            Resource.error(
                                msg = recipes.message.toString(),
                                data = when (sortBy) {
                                    POPULARITY ->
                                        recipes.data?.sortedByDescending { it.recipe.likes }
                                    MISSING_INGREDIENTS ->
                                        recipes.data?.sortedBy { it.recipe.missedIngredientCount }
                                }
                            )
                        }
                    }
                } else {
                    Resource.success(emptyList())
                }
            }

        return recipes.flowOn(ioDispatcher)
    }

    private suspend fun findSaveableRecipes(ingredientNames: List<String>): Resource<List<SaveableRecipe>> {
        val recipes = recipeRepository.get().findRecipes(
            fetchStrategy = getFetchStrategy(),
            ingredients = ingredientNames
        )

        // UseCase handles success/error state
        // TODO: duplicate when block with same conditions
        return when (recipes.status) {
            SUCCESS -> {
                Resource.success(
                    data = recipes.data?.let {
                        it.map { recipe ->
                            // TODO: make this a bulk operation -- many segmented DB reads this way
                            //  Also - this is retriggered when we sort for each element in list; unnecessary
                            //  if list contents haven't changed. Look into caching the results to re-use
                            //  specifically for sorting
                            //  Have this function return a list of [SaveableRecipes] where we mark each one
                            //  on whether it was saved
                            val isRecipeSaved = recipeRepository.get().isRecipeSaved(recipe.id)
                            SaveableRecipe(
                                recipe = recipe,
                                isSaved = isRecipeSaved
                            )
                        }
                    }
                )
            }
            ERROR -> {
                Resource.error(
                    msg = recipes.message.toString(),
                    data = recipes.data?.let {
                        it.map { recipe ->
                            // TODO: make this a bulk operation -- many segmented DB reads this way
                            //  Also - this is retriggered when we sort for each element in list; unnecessary
                            //  if list contents haven't changed. Look into caching the results to re-use
                            //  specifically for sorting
                            //  Have this function return a list of [SaveableRecipes] where we mark each one
                            //  on whether it was saved
                            val isRecipeSaved = recipeRepository.get().isRecipeSaved(recipe.id)
                            SaveableRecipe(
                                recipe = recipe,
                                isSaved = isRecipeSaved
                            )
                        }
                    }
                )
            }
        }
    }


    private fun getPantryIngredients(): Flow<List<String>> {
        return pantryRepository
            .get()
            .listPantry()
            .map { ingredientList ->
                ingredientList
                    .map { item ->
                        item.ingredient.name
                    }
            }
    }

    private fun getFetchStrategy() =
        // TODO: Think about adding `&& networkDialogShownForRecipesTab` or similar to prevent
        //  network error dialog from showing every time sort order is changed
        when (pantryStateManager.get().hasPantryStateChanged()) {
            true -> FetchStrategy.NETWORK
            false -> FetchStrategy.DATABASE
        }
}

enum class SortBy {
    POPULARITY,
    MISSING_INGREDIENTS
}

val DEFAULT_SORTING = POPULARITY
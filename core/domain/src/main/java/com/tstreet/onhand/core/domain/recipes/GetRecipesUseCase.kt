package com.tstreet.onhand.core.domain.recipes

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.common.CommonModule.IO
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
                    when (sortBy) {
                        POPULARITY ->
                            Resource.success(recipes.data?.sortedByDescending { it.recipe.likes })
                        MISSING_INGREDIENTS ->
                            Resource.success(recipes.data?.sortedBy { it.recipe.missedIngredientCount })
                    }.also {
                        pantryStateManager.get().onResetPantryState()
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

        return when (recipes.status) {
            Status.SUCCESS -> {
                Resource.success(recipes.data!!.map { recipe ->
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
                })
            }
            Status.ERROR -> {
                Resource.error(msg = recipes.message.toString(), data = emptyList())
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

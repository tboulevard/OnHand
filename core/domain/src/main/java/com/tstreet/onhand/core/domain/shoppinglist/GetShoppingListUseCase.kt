package com.tstreet.onhand.core.domain.shoppinglist

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.PantryStateManager
import com.tstreet.onhand.core.common.SavedRecipeStateManager
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class GetShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
    private val pantryRepository: Provider<PantryRepository>,
    private val recipeRepository: Provider<RecipeRepository>,
    private val pantryStateManager: Provider<PantryStateManager>,
    private val savedRecipeStateManager: Provider<SavedRecipeStateManager>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    @OptIn(FlowPreview::class)
    operator fun invoke(): Flow<List<ShoppingListIngredient>> {
        println("[OnHand] GetShoppingListUseCase.invoke()")
        // TODO: logic here is a bit wasteful since pantry state won't be reset until we navigate
        //  to the recipe search screen. Also doesn't make sense to reset pantry state here as
        //  that would prevent recipe search listings from updating. So: We only retrieve cached
        //  results if recipe search screen has been visited and cache is not empty. Not ideal but
        //  it works
        val getShoppingListFlow =
            shouldRefreshShoppingList()
                .flatMapConcat { shouldRefresh ->
                    when {
                        shouldRefresh -> {
                            println("[OnHand] Generating new shopping list")
                            combine(
                                pantryRepository.get().listPantry(),
                                recipeRepository.get().getSavedRecipes()
                            ) { pantryIngredients, savedRecipes ->
                                getShoppingList(
                                    pantryItems = pantryIngredients,
                                    recipes = savedRecipes.map { it.recipe }
                                )
                            }.onEach {
                                println("[OnHand] Generating new shopping list, then caching it")
                                shoppingListRepository.get().clear()
                                shoppingListRepository
                                    .get()
                                    .insertIngredients(it)

                            }
                        }
                        else -> {
                            println("[OnHand] Retrieving cached shopping list")
                            shoppingListRepository.get().getShoppingList()
                        }
                    }
                }.onEach {
                    savedRecipeStateManager.get().onResetSavedRecipeState()
                }.catch {
                    // TODO: better error handling
                    println("[OnHand] Error retrieving shopping list. Error=${it.message}")
                    emit(emptyList())
                }

        return getShoppingListFlow.flowOn(ioDispatcher)
    }

    private fun shouldRefreshShoppingList(): Flow<Boolean> {
        return flow {
            emit(
                shoppingListRepository.get().isEmpty() ||
                        pantryStateManager.get().hasPantryStateChanged() ||
                        savedRecipeStateManager.get().hasSavedRecipeStateChanged()
            )
        }
    }

    private suspend fun getShoppingList(
        pantryItems: List<PantryIngredient>,
        recipes: List<Recipe>
    ): List<ShoppingListIngredient> {
        println("[OnHand] getShoppingList($pantryItems, $recipes)")

        //  Collect all ingredients (used and missed) in all saved recipes. We collect used and
        //  missed in case pantry state no longer reflects the saved recipe ingredient state (i.e.
        //  pantry state changes since the recipe was saved with what ingredients were missing at
        //  the time...)
        val ingredientUsageMap = mutableMapOf<String, IngredientUsage>()
        recipes.forEach { recipe ->
            collectIngredientsForRecipe(
                mapping = ingredientUsageMap,
                recipe = recipe
            )
        }

        // Iterate over items in pantry and ingredients in all recipes. For now we just entirely
        // remove the ingredient from the mapping if it's in the pantry already
        pantryItems.forEach {
            if (ingredientUsageMap.keys.contains(it.ingredient.name)) {
                ingredientUsageMap.remove(it.ingredient.name)
            }
        }

        // Create shopping list by flattening the recipeToIngredientMap into a list of
        // ShoppingListIngredient
        return ingredientUsageMap.map {
            ShoppingListIngredient(
                name = it.value.name,
                mappedRecipes = it.value.recipes,
                isPurchased = shoppingListRepository.get().isIngredientCheckedOff(it.key)
            )
        }
    }

    /**
     * Given an empty [mapping] of ingredient name to [IngredientUsage] and a [recipe], we iterate
     * over used/missed ingredients from [recipe] to construct a mapping of ingredient to recipes
     * the ingredient is included in.
     */
    private fun collectIngredientsForRecipe(
        mapping: MutableMap<String, IngredientUsage>,
        recipe: Recipe
    ) {
        recipe.usedIngredients.plus(recipe.missedIngredients).forEach { recipeIngredient ->
            // If we already created an entry in the map, add to it
            if (mapping.containsKey(recipeIngredient.ingredient.name)) {
                mapping[recipeIngredient.ingredient.name]?.recipes?.add(recipe)
            } else { // Otherwise, create it
                mapping[recipeIngredient.ingredient.name] = IngredientUsage(
                    name = recipeIngredient.ingredient.name
                ).also {
                    it.recipes.add(recipe)
                }
            }
        }
    }
}

/**
 * Intermediate class to help construct the list of [ShoppingListIngredient]s.
 */
private class IngredientUsage(
    val name: String,
    val recipes: MutableList<Recipe> = mutableListOf()
)

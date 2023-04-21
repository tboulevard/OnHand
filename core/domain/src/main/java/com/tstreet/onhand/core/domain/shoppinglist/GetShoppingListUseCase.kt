package com.tstreet.onhand.core.domain.shoppinglist

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.PantryStateManager
import com.tstreet.onhand.core.common.SavedRecipeStateManager
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.PantryRepository
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.data.repository.ShoppingListRepository
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
        val getShoppingListFlow = flow {
            emit(
                shoppingListRepository.get().isEmpty() ||
                        pantryStateManager.get().hasPantryStateChanged() ||
                        savedRecipeStateManager.get().hasSavedRecipeStateChanged()
            )
        }.flatMapConcat { shouldRefreshShoppingList ->
            if (shouldRefreshShoppingList) {
                println("[OnHand] Generating new shopping list")
                combine(
                    pantryRepository.get().listPantry(),
                    recipeRepository.get().getSavedRecipes()
                ) { pantryIngredients, savedRecipes ->
                    getShoppingList(
                        pantry = pantryIngredients,
                        recipes = savedRecipes.map { it.recipe }
                    )
                }.onEach {
                    println("[OnHand] Generating new shopping list, then caching it")
                    shoppingListRepository.get().clear()
                    shoppingListRepository
                        .get()
                        .insertIngredients(it)

                }
            } else {
                println("[OnHand] Retrieving cached shopping list")
                shoppingListRepository.get().getShoppingList()
            }
        }.onEach { savedRecipeStateManager.get().onResetSavedRecipeState() }
            .catch {
                // TODO: better error handling, and make sure this actually works.
                println("[OnHand] Error retrieving shopping list. Error=${it.message}")
                emit(emptyList())
            }

        return getShoppingListFlow.flowOn(ioDispatcher)
    }

    private suspend fun getShoppingList(
        pantry: List<PantryIngredient>,
        recipes: List<Recipe>
    ): List<ShoppingListIngredient> {
        println("[OnHand] getShoppingList($pantry, $recipes)")

        //  Collect all ingredients in all saved recipes (used and missed), totaling amounts. We collect
        //  used and missed in case pantry state no longer reflects the saved recipe ingredient state
        //  (i.e. pantry state changes since the recipe was saved with what ingredients were missing at
        //  the time...)

        // TODO: use id for all mappings because some ingredients have same id diff string, causing the
        //  containtskey constraint to fail
        val recipeMeasureMap = mutableMapOf<Int, IngredientMappedToRecipes>()
        recipes.forEach { recipe ->
            recipe.usedIngredients.forEach { recipeIngredient ->
                // TODO: revisit if this is all necessary given that class contains empty mutable list
                // If we already created a list in the map, add to it
                if (recipeMeasureMap.containsKey(recipeIngredient.ingredient.id)) {
                    recipeMeasureMap[recipeIngredient.ingredient.id]!!.recipeMeasures.add(
                        RecipeMeasure(recipe, recipeIngredient.unit, recipeIngredient.amount)
                    )
                } else { // Otherwise, create it
                    recipeMeasureMap[recipeIngredient.ingredient.id] = IngredientMappedToRecipes(
                        id = recipeIngredient.ingredient.id,
                        name = recipeIngredient.ingredient.name,
                    ).also {
                        it.recipeMeasures.add(
                            RecipeMeasure(
                                recipe,
                                recipeIngredient.unit,
                                recipeIngredient.amount
                            )
                        )
                    }
                }
            }
            recipe.missedIngredients.forEach { recipeIngredient ->
                if (recipeMeasureMap.containsKey(recipeIngredient.ingredient.id)) {
                    recipeMeasureMap[recipeIngredient.ingredient.id]!!.recipeMeasures.add(
                        RecipeMeasure(recipe, recipeIngredient.unit, recipeIngredient.amount)
                    )
                } else { // Otherwise, create it
                    recipeMeasureMap[recipeIngredient.ingredient.id] = IngredientMappedToRecipes(
                        id = recipeIngredient.ingredient.id,
                        name = recipeIngredient.ingredient.name,
                    ).also {
                        it.recipeMeasures.add(
                            RecipeMeasure(
                                recipe,
                                recipeIngredient.unit,
                                recipeIngredient.amount
                            )
                        )
                    }
                }
            }
        }

        // Iterate over items in pantry and ingredients in all recipes. For first iteration we
        // just entirely remove the item if we have it in the pantry - TODO deal with quantities
        // later by subtraction. Issue right now is that ingredients are measures using different
        // units.
        pantry.forEach {
            if (recipeMeasureMap.keys.contains(it.ingredient.id)) {
                recipeMeasureMap.remove(it.ingredient.id)
            }
        }

        // Create shopping list by flattening the recipeMeasureMap
        return recipeMeasureMap.map {
            ShoppingListIngredient(
                id = it.key,
                name = it.value.name,
                recipeMeasures = it.value.recipeMeasures,
                isPurchased = shoppingListRepository.get().isIngredientCheckedOff(it.key)
            )
        }
    }
}

class IngredientMappedToRecipes(
    val id: Int,
    val name: String,
    val recipeMeasures: MutableList<RecipeMeasure> = mutableListOf()
)

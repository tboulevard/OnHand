package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.PantryRepository
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.data.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class GetShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
    private val pantryRepository: Provider<PantryRepository>,
    private val recipeRepository: Provider<RecipeRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    // TODO: If I add to pantry the ingredient I'm missing, it won't update
    // TODO: cache shopping list if saved recipes haven't changed
    // TODO: are we handling flow of
    //  1. add ingredients to pantry
    //  2. save recipe shown bc we have the ingredients
    //  3. remove ingredient from pantry (but still have recipe saved)
    operator fun invoke(): Flow<List<ShoppingListIngredient>> {
        println("[OnHand] GetShoppingListUseCase.invoke()")
        return combine(
            pantryRepository.get().listPantry(),
            recipeRepository.get().getSavedRecipes()
        ) { pantryIngredients, savedRecipes ->
            getShoppingList(
                pantry = pantryIngredients,
                recipes = savedRecipes
            )
        }.onEach {
            // TODO: inserting the same ingredient from multiple recipes breaks primary key
            //  constraint for Room and throws exception - revisit in next PR
            //shoppingListRepository.get().insertShoppingList(it)
        }.flowOn(ioDispatcher)
    }

    // TODO: Use pantry to determine if we actually have enough quantity of item later. Can do simple math in this function
    private fun getShoppingList(
        pantry: List<PantryIngredient>,
        recipes: List<Recipe>
    ): List<ShoppingListIngredient> {
        println("[OnHand] getShoppingList($pantry, $recipes)")

        // Determine shopping list from ingredients + saved recipes
        // 1. Collect all ingredients in all saved recipes (used and missed), totaling amounts. We collect
        //  used and missed in case pantry state no longer reflects the saved recipe ingredient state
        //  (i.e. pantry state changes since the recipe was saved with what ingredients were missing at
        //  the time...)
        // 2. Double for loop over ^ list, outer loop on pantry ingredients
        // TODO: 3. subtract amounts (just remove the ingredient initially since we don't store quantities)
        // 4. Create shopping list, interleaving amounts and units if an ingredient is a member of
        //  multiple recipes

        // 1)
        val allRecipeIngredients = mutableMapOf<Ingredient, MutableList<RecipeMeasure>>()
        recipes.forEach { recipe ->
            recipe.usedIngredients.forEach { recipeIngredient ->
                // If we already created a list in the map, add to it
                if (allRecipeIngredients.containsKey(recipeIngredient.ingredient)) {
                    allRecipeIngredients[recipeIngredient.ingredient]!!.add(
                        RecipeMeasure(recipe, recipeIngredient.unit, recipeIngredient.amount)
                    )
                } else { // Otherwise, create it
                    allRecipeIngredients[recipeIngredient.ingredient] =
                        mutableListOf(
                            RecipeMeasure(
                                recipe,
                                recipeIngredient.unit,
                                recipeIngredient.amount
                            )
                        )
                }
            }
            recipe.missedIngredients.forEach { recipeIngredient ->
                if (allRecipeIngredients.containsKey(recipeIngredient.ingredient)) {
                    allRecipeIngredients[recipeIngredient.ingredient]!!.add(
                        RecipeMeasure(recipe, recipeIngredient.unit, recipeIngredient.amount)
                    )
                } else {
                    allRecipeIngredients[recipeIngredient.ingredient] =
                        mutableListOf(
                            RecipeMeasure(
                                recipe,
                                recipeIngredient.unit,
                                recipeIngredient.amount
                            )
                        )
                }
            }
        }

        // 2) - For first iteration just entirely remove the item if we have it in the pantry.
        //  deal with quantities later...
        // TODO 3)
        pantry.forEach {
            if (allRecipeIngredients.keys.contains(it.ingredient)) {
                allRecipeIngredients.remove(it.ingredient)
            }
        }

        // 4)
        val shoppingList = allRecipeIngredients.map { curr ->
            ShoppingListIngredient(
                id = curr.key.id,
                name = curr.key.name,
                recipeMeasures = curr.value.map {
                    RecipeMeasure(
                        recipe = it.recipe,
                        unit = it.unit,
                        amount = it.amount
                    )
                }
            )
        }

        return shoppingList
    }
}

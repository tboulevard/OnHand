package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.PantryRepository
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.data.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.PantryIngredient
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.ShoppingListIngredient
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
        // 4. Create shopping list

        // 1)
        val allRecipeIngredients = mutableMapOf<Int, MutableList<RecipeMeasure>>()
        recipes.forEach { recipe ->
            recipe.usedIngredients.forEach { recipeIngredient ->
                if (allRecipeIngredients.containsKey(recipeIngredient.ingredient.id)) {
                    allRecipeIngredients[recipeIngredient.ingredient.id]!!.add(
                        RecipeMeasure(recipe, recipeIngredient.unit, recipeIngredient.amount)
                    )
                } else {
                    allRecipeIngredients[recipeIngredient.ingredient.id] =
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
                if (allRecipeIngredients.containsKey(recipeIngredient.ingredient.id)) {
                    allRecipeIngredients[recipeIngredient.ingredient.id]!!.add(
                        RecipeMeasure(recipe, recipeIngredient.unit, recipeIngredient.amount)
                    )
                } else {
                    allRecipeIngredients[recipeIngredient.ingredient.id] =
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
            if (allRecipeIngredients.keys.contains(it.ingredient.id)) {
                allRecipeIngredients.remove(it.ingredient.id)
            }
        }

        // TODO: figure out a way to interleave amounts and units
        //  e.g.: instead of: 1.25 + 4 cup, oz -> 1.25 cup, 4 oz
        // 4)
        val shoppingList = allRecipeIngredients.map { curr ->
            ShoppingListIngredient(
                id = curr.key,
                name = curr.key.toString(),
                amount = {
                    var str = ""
                    curr.value.forEachIndexed { index, measure ->
                        str += measure.amount.toString()

                        if (index != (curr.value.size - 1)) {
                            str += " + "
                        }
                    }
                    str
                },
                mappedRecipes = { curr.value.map { it.recipe } },
                unit = {
                    var str = ""
                    curr.value.forEachIndexed { index, measure ->
                        str += measure.unit

                        if (index != (curr.value.size - 1)) {
                            str += ", "
                        }
                    }
                    str
                }
            )
        }

        return shoppingList
    }
}

class RecipeMeasure(
    val recipe: Recipe,
    val unit: String,
    val amount: Double
)
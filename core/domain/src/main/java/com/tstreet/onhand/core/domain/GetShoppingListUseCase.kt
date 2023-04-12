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
            // TODO: Cache shopping list depending on pantry state to save on work
            //shoppingListRepository.get().insertShoppingList(it)
        }.flowOn(ioDispatcher)
    }

    private fun getShoppingList(
        pantry: List<PantryIngredient>,
        recipes: List<Recipe>
    ): List<ShoppingListIngredient> {
        println("[OnHand] getShoppingList($pantry, $recipes)")

        //  Collect all ingredients in all saved recipes (used and missed), totaling amounts. We collect
        //  used and missed in case pantry state no longer reflects the saved recipe ingredient state
        //  (i.e. pantry state changes since the recipe was saved with what ingredients were missing at
        //  the time...)
        val recipeMeasureMap = mutableMapOf<Ingredient, MutableList<RecipeMeasure>>()
        recipes.forEach { recipe ->
            recipe.usedIngredients.forEach { recipeIngredient ->
                // If we already created a list in the map, add to it
                if (recipeMeasureMap.containsKey(recipeIngredient.ingredient)) {
                    recipeMeasureMap[recipeIngredient.ingredient]!!.add(
                        RecipeMeasure(recipe, recipeIngredient.unit, recipeIngredient.amount)
                    )
                } else { // Otherwise, create it
                    recipeMeasureMap[recipeIngredient.ingredient] =
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
                if (recipeMeasureMap.containsKey(recipeIngredient.ingredient)) {
                    recipeMeasureMap[recipeIngredient.ingredient]!!.add(
                        RecipeMeasure(recipe, recipeIngredient.unit, recipeIngredient.amount)
                    )
                } else {
                    recipeMeasureMap[recipeIngredient.ingredient] =
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

        // Iterate over items in pantry and ingredients in all recipes. For first iteration we
        // just entirely remove the item if we have it in the pantry - TODO deal with quantities
        // later by subtraction. Issue right now is that ingredients are measures using different
        // units.
        pantry.forEach {
            if (recipeMeasureMap.keys.contains(it.ingredient)) {
                recipeMeasureMap.remove(it.ingredient)
            }
        }

        // Create shopping list by flattening the recipeMeasureMap
        return recipeMeasureMap.map {
            ShoppingListIngredient(
                id = it.key.id,
                name = it.key.name,
                recipeMeasures = it.value
            )
        }
    }
}

package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.PantryRepository
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.data.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.CompositeRecipe
import com.tstreet.onhand.core.model.PantryIngredient
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
            //  constraint for Room and throws exception
            //shoppingListRepository.get().insertShoppingList(it)
        }.flowOn(ioDispatcher)
    }

    // TODO: Use pantry to determine if we actually have enough quantity of item later. Can do simple math in this function
    private fun getShoppingList(
        pantry: List<PantryIngredient>,
        recipes: List<CompositeRecipe>
    ): List<ShoppingListIngredient> {
        println("[OnHand] getShoppingList($pantry, $recipes)")

        val shoppingList = mutableListOf<ShoppingListIngredient>()

        for(recipe in recipes) {
            shoppingList.addAll(recipe.missedIngredients.map {
                ShoppingListIngredient(
                    id = it.ingredient.id,
                    name = it.ingredient.name,
                    amount = it.amount,
                    unit = it.unit,
                    // TODO: multi recipe mappings
                    mappedRecipes = mutableListOf(recipe)
                )
            })
        }

        return shoppingList
    }
}
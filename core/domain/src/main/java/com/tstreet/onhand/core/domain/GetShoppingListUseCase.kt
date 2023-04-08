package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.PantryRepository
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.data.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.CompositeRecipe
import com.tstreet.onhand.core.model.Ingredient
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
            shoppingListRepository.get().insertShoppingList(it)
        }.flowOn(ioDispatcher)
    }

    private fun getShoppingList(
        pantry: List<Ingredient>,
        recipes: List<CompositeRecipe>
    ): List<ShoppingListIngredient> {
        return emptyList()
    }
}
package com.tstreet.onhand.core.domain.usecase.shoppinglist

import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.model.*
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.PantryIngredient
import com.tstreet.onhand.core.model.data.ShoppingListIngredient
import com.tstreet.onhand.core.model.domain.ShoppingListResult
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class GetShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
    private val pantryRepository: Provider<PantryRepository>
) : UseCase() {

    operator fun invoke(): Flow<ShoppingListResult> {
        return combineShoppingListAndRecipes(
            getShoppingList(),
            getRecipesInShoppingList(),
            getPantryList()
        ).catch {
            emit(ShoppingListResult.Error)
        }.onStart {
            emit(ShoppingListResult.Loading)
        }
    }

    private fun getShoppingList(): Flow<Resource<List<ShoppingListIngredient>>> {
        return flow {
            emit(
                shoppingListRepository
                    .get()
                    .getShoppingList()
            )
        }
    }

    private fun getRecipesInShoppingList(): Flow<Resource<List<RecipePreview>>> {
        return flow {
            emit(
                shoppingListRepository
                    .get()
                    .getRecipesInShoppingList()
            )
        }
    }
    
    private fun getPantryList(): Flow<List<Ingredient>> =
        flow { emit(pantryRepository.get().listPantry()) }

    /**
     * Given a [Flow] of [ShoppingListIngredient]s and a [Flow] of [RecipePreview]s and merge them
     * into a [ShoppingListResult].
     *
     * TODO: Decide later whether shopping list should recipes if there are somehow no ingredients.
     */
    private fun combineShoppingListAndRecipes(
        shoppingListFlow: Flow<Resource<List<ShoppingListIngredient>>>,
        recipesInShoppingListFlow: Flow<Resource<List<RecipePreview>>>,
        pantryListFlow: Flow<List<Ingredient>>
    ): Flow<ShoppingListResult> =
        combine(
            shoppingListFlow,
            recipesInShoppingListFlow,
            pantryListFlow
        ) { shoppingListIngredients, recipesInShoppingList, pantryIngredients ->
            ShoppingListResult.Success(
                ingredients = when (shoppingListIngredients.status) {
                    Status.SUCCESS -> {
                        (shoppingListIngredients.data as List<ShoppingListIngredient>).map {
                            it.copy(inPantry = pantryIngredients.contains(it.ingredient))
                        }
                    }

                    Status.ERROR -> {
                        emptyList()
                    }
                }
            )
        }
}

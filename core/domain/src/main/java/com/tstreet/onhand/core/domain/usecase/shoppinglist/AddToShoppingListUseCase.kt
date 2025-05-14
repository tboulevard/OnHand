package com.tstreet.onhand.core.domain.usecase.shoppinglist

import android.util.Log
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.ShoppingListIngredient
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class AddToShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>
) : UseCase() {

    suspend fun addIngredients(
        missingIngredients: List<Ingredient>,
        recipe: RecipePreview? = null
    ): Resource<Unit> {
        Log.d("[OnHand]", "Adding ingredients=$missingIngredients, recipe=$recipe to shopping list")
        val result = shoppingListRepository.get().addIngredients(
            missingIngredients.map {
                ShoppingListIngredient(
                    it.name,
                    recipe,
                    false
                )
            }
        )

        return when (result.status) {
            Status.SUCCESS -> {
                Resource.success(Unit)
            }

            Status.ERROR -> {
                Resource.error(msg = result.message.toString())
            }
        }
    }

    suspend fun addShoppingListIngredients(
        missingIngredients: List<ShoppingListIngredient>,
        recipe: RecipePreview? = null
    ): Resource<Unit> {
        Log.d("[OnHand]", "Adding ingredients=$missingIngredients, recipe=$recipe to shopping list")
        val result = shoppingListRepository.get().addIngredients(missingIngredients)

        return when (result.status) {
            Status.SUCCESS -> {
                Resource.success(Unit)
            }

            Status.ERROR -> {
                Resource.error(msg = result.message.toString())
            }
        }
    }
}

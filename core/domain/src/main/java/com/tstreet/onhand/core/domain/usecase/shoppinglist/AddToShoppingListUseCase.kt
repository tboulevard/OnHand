package com.tstreet.onhand.core.domain.usecase.shoppinglist

import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.ShoppingListIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
class AddToShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator fun invoke(
        ingredients: List<Ingredient>,
        recipePreview: RecipePreview? = null
    ): Flow<Resource<Unit>> {
        // TODO: using flow here is probably unnecessary - look into proper way to run suspending
        // function on diff dispatcher (using flow API for now so we don't use ViewModel coroutine
        // dispatcher)
        return flow {
            Log.d("[OnHand]", "Adding ingredients=$ingredients, recipe=$recipePreview to shopping list")
            val result = shoppingListRepository.get().insertIngredients(
                ingredients.map {
                    ShoppingListIngredient(
                        it.name,
                        recipePreview,
                        false
                    )
                }
            )

            when (result.status) {
                Status.SUCCESS -> {
                    emit(Resource.success(null))
                }
                Status.ERROR -> {
                    // TODO: ViewModel layer ignores the full stacktrace - keeping here as reminder
                    //  to transmit via analytics here
                    Log.d("[OnHand]", "Error adding to shopping list - ${result.message}")
                    emit(Resource.error(msg = result.message.toString()))
                }
            }
        }.flowOn(ioDispatcher)
    }
}

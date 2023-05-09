package com.tstreet.onhand.core.domain.shoppinglist

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.ShoppingListIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class AddToShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    // TODO: using flow here is probably unncessary - look into proper way to run suspending
    // function on diff dipatcher (using flow API for now so we don't use ViewModel coroutine
    // dispatcher)
    operator fun invoke(
        ingredients: List<Ingredient>,
        recipe: Recipe? = null
    ): Flow<Resource<Unit>> {
        return flow {
            println("[OnHand] Adding $ingredients to shopping list")
            // TODO: this runs on viewmodel scope, fix later
            val result = shoppingListRepository.get().insertIngredients(
                ingredients.map {
                    ShoppingListIngredient(
                        it.name,
                        recipe,
                        false
                    )
                }
            )

            when (result.status) {
                Status.SUCCESS -> {
                    emit(Resource.success(null))
                }
                Status.ERROR -> {
                    emit(Resource.error(msg = result.message.toString()))
                }
            }
        }.flowOn(ioDispatcher)
    }
}

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

    operator fun invoke(
        ingredients: List<Ingredient>,
        recipe: Recipe? = null
    ): Flow<Resource<Unit>> {
        println("[OnHand] Adding $ingredients to shopping list")

        return flow<Resource<Unit>> {
            val insertResult = shoppingListRepository.get().insertIngredients(
                ingredients.map {
                    ShoppingListIngredient(
                        it.name,
                        recipe, // TODO: add recipe
                        false
                    )
                }
            )

            when (insertResult.status) {
                Status.SUCCESS -> {
                    emit(Resource.success(null))
                }
                Status.ERROR -> {
                    emit(Resource.error(msg = insertResult.message.toString()))
                }
            }
        }.flowOn(ioDispatcher)
    }
}

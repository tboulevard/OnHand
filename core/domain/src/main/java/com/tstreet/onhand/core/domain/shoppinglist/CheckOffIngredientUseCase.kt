package com.tstreet.onhand.core.domain.shoppinglist

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.ShoppingListIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
class CheckOffIngredientUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : UseCase() {

    operator fun invoke(shoppingListIngredient: ShoppingListIngredient): Flow<Resource<Unit>> {
        println("[OnHand] Marking shoppingListIngredient=${shoppingListIngredient}")
        return flow {
            val result = shoppingListRepository
                .get()
                .checkOffIngredient(shoppingListIngredient)

            when (result.status) {
                Status.SUCCESS -> {
                    emit(Resource.success(null))
                }
                Status.ERROR -> {
                    emit(
                        Resource.error(
                            msg = "There was a problem checking off the ingredient in your " +
                                    "shopping list. Please try again."
                        )
                    )
                }
            }
        }.flowOn(ioDispatcher)
    }
}

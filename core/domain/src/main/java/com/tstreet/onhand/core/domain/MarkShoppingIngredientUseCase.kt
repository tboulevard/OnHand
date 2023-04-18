package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.ShoppingListIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
// TODO: redo naming for everything related to this use case
class MarkShoppingIngredientUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : UseCase() {

    operator fun invoke(shoppingListIngredient: ShoppingListIngredient): Flow<Boolean> {
        println("[OnHand] Marking shoppingListIngredient=${shoppingListIngredient}")
        return flow {
            shoppingListRepository
                .get()
                .markIngredientPurchased(shoppingListIngredient)
            emit(true)
        }.flowOn(ioDispatcher)
            .catch {
                // TODO: better error handling
                println("[OnHand] Error marking $shoppingListIngredient to database. Error=${it.message}")
                emit(false)
            }
    }
}
package com.tstreet.onhand.core.domain.usecase.shoppinglist

import android.util.Log
import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.model.*
import com.tstreet.onhand.core.model.data.ShoppingListIngredient
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class GetShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
) : UseCase() {

    operator fun invoke(): Flow<Resource<List<ShoppingListIngredient>>> {
        Log.d("[OnHand]", "GetShoppingListUseCase.invoke()")
        return flow {
            emit(
                shoppingListRepository
                    .get()
                    .getShoppingList()
            )
        }
    }
}

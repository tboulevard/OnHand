package com.tstreet.onhand.core.domain.shoppinglist

import android.util.Log
import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Provider

class GetShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
) : UseCase() {

    operator fun invoke(): Flow<Resource<List<ShoppingListIngredient>>> {
        Log.d("[OnHand]", "GetShoppingListUseCase.invoke()")
        return shoppingListRepository
            .get()
            .getShoppingList()
    }
}

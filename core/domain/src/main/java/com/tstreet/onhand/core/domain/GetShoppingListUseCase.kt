package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.UseCase
import javax.inject.Inject

class GetShoppingListUseCase @Inject constructor() : UseCase() {

    operator fun invoke() {
        println("[OnHand] GetShoppingListUseCase.invoke()")
    }
}
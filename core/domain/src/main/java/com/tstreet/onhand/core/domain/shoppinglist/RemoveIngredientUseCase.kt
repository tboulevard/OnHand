package com.tstreet.onhand.core.domain.shoppinglist

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.ShoppingListIngredient
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class RemoveIngredientUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
) : UseCase() {

    suspend operator fun invoke(shoppingListIngredient: ShoppingListIngredient): Resource<Unit> {
        return shoppingListRepository
            .get()
            .removeIngredient(shoppingListIngredient)
    }
}

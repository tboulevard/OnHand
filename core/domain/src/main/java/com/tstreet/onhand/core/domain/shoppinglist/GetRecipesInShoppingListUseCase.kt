package com.tstreet.onhand.core.domain.shoppinglist

import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.Recipe
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Provider

class GetRecipesInShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
) : UseCase() {

    operator fun invoke(): Flow<Resource<List<Recipe>>> {
        return shoppingListRepository
            .get()
            .getRecipesInShoppingList()
    }
}

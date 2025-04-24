package com.tstreet.onhand.core.domain.usecase.shoppinglist

import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.RecipePreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Provider

class GetRecipesInShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
) : UseCase() {

    operator fun invoke(): Flow<Resource<List<RecipePreview>>> {
        return shoppingListRepository
            .get()
            .getRecipesInShoppingList()
    }
}

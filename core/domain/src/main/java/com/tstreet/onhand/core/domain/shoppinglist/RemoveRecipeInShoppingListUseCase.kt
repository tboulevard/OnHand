package com.tstreet.onhand.core.domain.shoppinglist

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class RemoveRecipeInShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator suspend fun invoke(recipe: Recipe): Resource<Unit> {
        println("[OnHand] RemoveRecipeInShoppingListUseCase.invoke()")
        return withContext(ioDispatcher) {
            val result = shoppingListRepository.get().removeRecipe(recipe)
            when (result.status) {
                Status.SUCCESS -> {
                    Resource.success(result.data)
                }
                Status.ERROR -> {
                    Resource.error(result.message.toString())
                }
            }
        }
    }
}

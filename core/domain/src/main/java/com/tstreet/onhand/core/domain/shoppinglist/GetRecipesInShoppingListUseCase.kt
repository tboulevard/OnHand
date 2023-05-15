package com.tstreet.onhand.core.domain.shoppinglist

import com.tstreet.onhand.core.common.CommonModule
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class GetRecipesInShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
    @Named(CommonModule.IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator fun invoke(): Flow<Resource<List<Recipe>>> {
        // TODO: using flow here is probably unnecessary - look into proper way to run suspending
        // function on diff dispatcher (using flow API for now so we don't use ViewModel coroutine
        // dispatcher)
        val getRecipesInShoppingListFlow = flow {
            println("[OnHand] Getting recipes in shopping list")
            val result = shoppingListRepository.get().getRecipesInShoppingList()
            when (result.status) {
                Status.SUCCESS -> {
                    emit(Resource.success(result.data))
                }
                Status.ERROR -> {
                    emit(Resource.error(result.message.toString()))
                }
            }
        }

        return getRecipesInShoppingListFlow.flowOn(ioDispatcher)
    }
}

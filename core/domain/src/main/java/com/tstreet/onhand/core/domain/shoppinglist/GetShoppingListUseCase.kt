package com.tstreet.onhand.core.domain.shoppinglist

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class GetShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator fun invoke(): Flow<Resource<List<ShoppingListIngredient>>> {
        println("[OnHand] GetShoppingListUseCase.invoke()")
        val getShoppingListFlow = flow {
            val shoppingList = shoppingListRepository.get().getShoppingList()
            when (shoppingList.status) {
                Status.SUCCESS -> {
                    emit(Resource.success(shoppingList.data))
                }
                Status.ERROR -> {
                    emit(Resource.error(shoppingList.message.toString()))
                }
            }
        }

        return getShoppingListFlow.flowOn(ioDispatcher)
    }
}

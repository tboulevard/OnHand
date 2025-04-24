package com.tstreet.onhand.core.domain.usecase.shoppinglist

import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.RecipePreview
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class RemoveRecipeInShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator suspend fun invoke(recipePreview: RecipePreview): Resource<Unit> {
        Log.d("[OnHand]", "RemoveRecipeInShoppingListUseCase.invoke()")
        return withContext(ioDispatcher) {
            val result = shoppingListRepository.get().removeRecipePreview(recipePreview)
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

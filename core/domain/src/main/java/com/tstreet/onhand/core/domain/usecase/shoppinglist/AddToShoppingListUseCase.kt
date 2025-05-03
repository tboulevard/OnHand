package com.tstreet.onhand.core.domain.usecase.shoppinglist

import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.ShoppingListIngredient
import com.tstreet.onhand.core.model.data.RecipeIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
class AddToShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: Provider<ShoppingListRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator suspend fun invoke(
        missingIngredients: List<RecipeIngredient>,
        recipe: RecipePreview? = null
    ): Flow<Resource<Unit>> {
        return flow {
            Log.d("[OnHand]", "Adding ingredients=$missingIngredients, recipe=$recipe to shopping list")
            val result = shoppingListRepository.get().addIngredients(
                missingIngredients.map {
                    ShoppingListIngredient(
                        it.ingredient.name,
                        recipe,
                        false
                    )
                }
            )

            when (result.status) {
                Status.SUCCESS -> {
                    emit(Resource.success(null))
                }
                Status.ERROR -> {
                    // TODO: ViewModel layer ignores the full stacktrace - keeping here as reminder
                    //  to transmit via analytics here
                    Log.d("[OnHand]", "Error adding to shopping list - ${result.message}")
                    emit(Resource.error(msg = result.message.toString()))
                }
            }
        }.flowOn(ioDispatcher)
    }
}

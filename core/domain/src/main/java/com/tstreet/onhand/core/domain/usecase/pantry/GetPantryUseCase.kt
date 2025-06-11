package com.tstreet.onhand.core.domain.usecase.pantry

import android.util.Log
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.model.data.IngredientCategory
import com.tstreet.onhand.core.model.domain.GetPantryResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class GetPantryUseCase @Inject constructor(
    private val repository: Provider<PantryRepository>,
) : UseCase() {

    /**
     * Retrieves the latest Pantry state.
     */
    suspend operator fun invoke(category: IngredientCategory = IngredientCategory.ALL): GetPantryResult {
        return try {
            GetPantryResult.Success(
                if (category == IngredientCategory.ALL) {
                    repository.get().listPantry()
                } else {
                    repository.get().listPantryByCategory(category)
                }
            )
        } catch (e: Exception) {
            Log.d("[OnHand]", "Error getting pantry", e)
            GetPantryResult.Error
        }
    }

    /**
     * Retrieves the pantry as a flow - changes in the underlying datastore are emitted.
     */
    fun observePantry(category: IngredientCategory = IngredientCategory.ALL): Flow<GetPantryResult> =
        if (category == IngredientCategory.ALL) {
            repository.get().listPantryFlow()
        } else {
            repository.get().listPantryByCategoryFlow(category)
        }
            .map {
                GetPantryResult.Success(it) as GetPantryResult
            }.catch {
                Log.d("[OnHand]", "Error getting pantry", it)
                emit(GetPantryResult.Error)
            }
}

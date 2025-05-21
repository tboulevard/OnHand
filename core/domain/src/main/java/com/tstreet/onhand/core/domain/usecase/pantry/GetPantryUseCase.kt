package com.tstreet.onhand.core.domain.usecase.pantry

import android.util.Log
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.model.data.PantryIngredient
import com.tstreet.onhand.core.model.domain.GetPantryResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class GetPantryUseCase @Inject constructor(
    private val repository: Provider<PantryRepository>,
) : UseCase() {

    operator fun invoke(): Flow<GetPantryResult> {
        return flow<GetPantryResult> {
            val pantry = repository.get().listPantry().map { PantryIngredient(it, true) }
            emit(GetPantryResult.Success(pantry))
        }.onStart {
            emit(GetPantryResult.Loading)
        }.catch {
            Log.d("[OnHand]", "Error getting pantry", it)
            emit(GetPantryResult.Error)
        }
    }
}

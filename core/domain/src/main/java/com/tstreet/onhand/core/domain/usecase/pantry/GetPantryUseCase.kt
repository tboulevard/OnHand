package com.tstreet.onhand.core.domain.usecase.pantry

import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.model.data.PantryIngredient
import com.tstreet.onhand.core.model.domain.PantryListResult
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

    operator fun invoke(): Flow<PantryListResult> {
        return flow<PantryListResult> {
            println("TESTINGTESTING: List Pantry - start")
            val pantry = repository.get().listPantry().map { PantryIngredient(it, true) }
            println("TESTINGTESTING: List Pantry - done")
            emit(PantryListResult.Success(pantry))
        }.onStart {
            println("TESTINGTESTING: Loading")
            emit(PantryListResult.Loading)
        }.catch {
            emit(PantryListResult.Error)
        }
    }
}

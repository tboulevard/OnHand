package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.PantryRepository
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.PantryIngredient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Provider

@FeatureScope
class GetPantryUseCase @Inject constructor(
    private val repository: Provider<PantryRepository>
) : UseCase() {

    operator fun invoke() : Flow<List<PantryIngredient>> {
        return repository
            .get()
            .listPantry()
    }
}

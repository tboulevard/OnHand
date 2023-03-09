package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.PantryRepository
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject
import javax.inject.Provider

class GetPantryUseCase @Inject constructor(
    private val repository: Provider<PantryRepository>
) : UseCase() {

    suspend operator fun invoke() : List<Ingredient> {
        return repository
            .get()
            .listPantry()
    }
}

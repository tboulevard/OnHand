package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.network.model.NetworkIngredient
import javax.inject.Inject
import javax.inject.Provider

class GetIngredientsUseCase @Inject constructor(
    private val repository: Provider<IngredientSearchRepository>
) : UseCase() {

    // TODO: Utilize the flow type (flow { ... })
    // TODO: Error and intermediate state handling
    operator fun invoke(prefix: String): List<Ingredient> =
        repository
            .get()
            .searchIngredients(prefix)
            // TODO: actually implement this from the DB layer
            .filter {
                it.name.contains(prefix)
            }
}
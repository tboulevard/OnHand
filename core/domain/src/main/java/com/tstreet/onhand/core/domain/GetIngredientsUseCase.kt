package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.network.model.NetworkIngredient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Provider

class GetIngredientsUseCase @Inject constructor(
    private val repository: Provider<IngredientSearchRepository>
) : UseCase() {

    // TODO: Utilize the flow type (flow { ... }) for
    // TODO: error and intermediate state handling here
    suspend operator fun invoke(prefix: String): List<Ingredient> =
        repository
            .get()
            .searchIngredients(
                prefix.lowercase()
            )
}
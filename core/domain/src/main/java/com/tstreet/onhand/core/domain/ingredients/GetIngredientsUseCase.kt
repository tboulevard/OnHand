package com.tstreet.onhand.core.domain.ingredients

import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.IngredientSearchRepository
import com.tstreet.onhand.core.model.PantryIngredient
import javax.inject.Inject
import javax.inject.Provider

// TODO: for some reason this scope isn't needed to bind use case to view lifecycle...is it
// TODO: because [IngredientSearchComponent] already specifies this? Either way, keeping
// TODO: here to be pedantic...
@FeatureScope
class GetIngredientsUseCase @Inject constructor(
    private val repository: Provider<IngredientSearchRepository>
) : UseCase() {

    suspend operator fun invoke(query: String): List<PantryIngredient> =
        repository
            .get()
            .searchIngredients(
                query.lowercase()
            )
}

package com.tstreet.onhand.core.domain.ingredients

import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.IngredientSearchRepository
import com.tstreet.onhand.core.model.PantryIngredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Provider

// TODO: for some reason this scope isn't needed to bind use case to view lifecycle...is it
// TODO: because [IngredientSearchComponent] already specifies this? Either way, keeping
// TODO: here to be pedantic...
@FeatureScope
class GetIngredientsUseCase @Inject constructor(
    private val repository: Provider<IngredientSearchRepository>
) : UseCase() {

    operator fun invoke(query: String?): Flow<List<PantryIngredient>> {
        val sanitizedQuery = query.sanitize()
        return when {
            sanitizedQuery.isNotBlank() -> {
                repository
                    .get()
                    .searchIngredients(sanitizedQuery)
            }
            else -> {
                flowOf(emptyList())
            }
        }
    }


    /**
     * Takes [this] and removes all non alphanumerics. If [this] is null we return an empty String.
     */
    private fun String?.sanitize(): String {
        return this?.replace(Regex("[^A-Za-z0-9]"), "")?.lowercase() ?: ""
    }
}

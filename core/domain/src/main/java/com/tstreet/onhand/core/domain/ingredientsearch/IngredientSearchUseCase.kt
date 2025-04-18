package com.tstreet.onhand.core.domain.ingredientsearch

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.IngredientSearchRepository
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.domain.IngredientSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class IngredientSearchUseCase @Inject constructor(
    private val ingredientRepository: IngredientSearchRepository,
    private val pantryRepository: PantryRepository
) : UseCase() {

    fun getPantryMapped(
        query: String,
        isExactIngredient: Boolean = false,
        limit: Int = 10,
    ): Flow<IngredientSearchResult> {

        if (query.isBlank()) {
            return flowOf(IngredientSearchResult.Empty)
        }

        return searchIngredients(query).mapItemsInPantry(
            pantryRepository.listPantry()
        )
            .catch {
                IngredientSearchResult.Error
            }
            .onStart {
                IngredientSearchResult.Loading
            }
    }

    operator fun invoke(
        query: String,
        isExactIngredient: Boolean = false,
        limit: Int = 10,
    ): Flow<IngredientSearchResult> {
        if (query.isBlank()) {
            return flowOf(IngredientSearchResult.Empty)
        }

        return searchIngredients(query)
            .map {
                IngredientSearchResult.Content(it)
            }
            .catch {
                IngredientSearchResult.Error
            }.onStart {
                IngredientSearchResult.Loading
            }
    }

    private fun searchIngredients(query: String): Flow<List<Ingredient>> {
        return ingredientRepository.searchIngredients(query)
    }


    /**
     * Given a [Flow] of [Ingredient]s and a [Flow] of [PantryIngredient]s, combine them
     * to create a list of Ingredients that are marked in pantry or not.
     */
    private fun Flow<List<Ingredient>>.mapItemsInPantry(
        pantryIngredients: Flow<List<Ingredient>>
    ): Flow<IngredientSearchResult> =
        this.combine(pantryIngredients) { searchIngredients, pantryItems ->
            IngredientSearchResult.Content(
                ingredients = searchIngredients.map { ingredient ->
                    Ingredient(
                        id = ingredient.id,
                        name = ingredient.name,
                        inPantry = pantryItems.find { ingredient.id == it.id } != null
                    )
                }
            )
        }
}
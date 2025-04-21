package com.tstreet.onhand.core.domain.ingredientsearch

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.api.repository.IngredientSearchRepository
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.PantryIngredient
import com.tstreet.onhand.core.model.domain.IngredientSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class IngredientSearchUseCase @Inject constructor(
    private val ingredientRepository: IngredientSearchRepository,
    private val pantryRepository: PantryRepository
) : UseCase() {

    fun observeIngredientsPantryMapped(
        query: String,
        isExactIngredient: Boolean = false,
        limit: Int = 10,
    ): Flow<IngredientSearchResult> {

        if (query.isBlank()) {
            return flowOf(IngredientSearchResult.Success(emptyList()))
        }

        return searchIngredients(query)
            .mapItemsInPantry()
            .catch {
                emit(IngredientSearchResult.Error)
            }.onStart {
                emit(IngredientSearchResult.Loading)
            }
    }

    private fun searchIngredients(query: String): Flow<List<Ingredient>> {
        return ingredientRepository.searchIngredients(query)
    }

    /**
     * Given a [Flow] of [Ingredient]s and a [Flow] of [PantryIngredient]s, combine them
     * to create a list of Ingredients that are marked in pantry or not.
     *
     * TODO: In a future iteration, make the referential equality more stable so we don't triggered
     *  recomposition for all list items UI each time one changes.
     */
    private fun Flow<List<Ingredient>>.mapItemsInPantry(): Flow<IngredientSearchResult> {
        return this.map { ingredients ->
            val pantrySet = pantryRepository.listPantry(ingredients).toSet()
            IngredientSearchResult.Success(
                ingredients = ingredients.map { ingredient ->
                    PantryIngredient(
                        ingredient = ingredient,
                        inPantry = pantrySet.contains(ingredient)
                    )
                }
            )
        }
    }
}
package com.tstreet.onhand.core.domain.usecase.ingredientsearch

import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.repository.IngredientSearchRepository
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.PantryIngredient
import com.tstreet.onhand.core.model.domain.IngredientSearchResult
import com.tstreet.onhand.core.model.domain.SuggestedIngredientsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@FeatureScope
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

    fun getSuggestedIngredients(): Flow<SuggestedIngredientsResult> {
        return suggestedIngredients()
            .mapSuggestions()
            .catch {
                emit(SuggestedIngredientsResult.Error)
            }.onStart {
                emit(SuggestedIngredientsResult.Loading)
            }
    }

    private fun searchIngredients(query: String): Flow<List<Ingredient>> {
        return ingredientRepository.searchIngredients(query)
    }

    private fun suggestedIngredients(): Flow<List<Ingredient>> {
        return ingredientRepository.mostPopularIngredients()
    }

    /**
     * Given a [Flow] of [Ingredient]s and a [Flow] of [PantryIngredient]s, combine them
     * to create a list of Ingredients that are marked in pantry or not.
     */
    private fun Flow<List<Ingredient>>.mapItemsInPantry(): Flow<IngredientSearchResult> =
        map { ingredients ->
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


    private fun Flow<List<Ingredient>>.mapSuggestions(): Flow<SuggestedIngredientsResult> =
        map { ingredients ->
            val pantrySet = pantryRepository.listPantry(ingredients).toSet()
            SuggestedIngredientsResult.Success(
                ingredients = ingredients.filter { ingredient ->
                    !pantrySet.contains(ingredient)
                }.map { nonPantryIngredient ->
                    PantryIngredient(
                        ingredient = nonPantryIngredient,
                        inPantry = false
                    )
                }
            )
        }
}
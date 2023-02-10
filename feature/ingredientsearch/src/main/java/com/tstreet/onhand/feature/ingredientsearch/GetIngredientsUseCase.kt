package com.tstreet.onhand.feature.ingredientsearch

import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.network.model.NetworkIngredient
import javax.inject.Inject

class GetIngredientsUseCase @Inject constructor(
    private val repository: IngredientSearchRepository
) {

    init {
        println("Creating ${this.javaClass.simpleName}")
    }

    // TODO: Utilize the flow type (flow { ... })
    // TODO: Error and intermediate state handling
    operator fun invoke(prefix: String): List<Ingredient> =
        repository
            .searchIngredients(prefix)
            .toExternalModel()

    private fun List<NetworkIngredient>.toExternalModel(): List<Ingredient> =
        this.map {
            Ingredient(
                name = it.name,
                image = it.image,
                childIngredient = it.children
            )
        }
}
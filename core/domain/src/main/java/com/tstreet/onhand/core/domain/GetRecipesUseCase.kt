package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.data.repository.RecipeSearchRepository
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.network.model.NetworkIngredient
import javax.inject.Inject

class GetRecipesUseCase @Inject constructor(
    private val repository: RecipeSearchRepository
) {

    init {
        println("Creating ${this.javaClass.simpleName}")
    }

    // TODO: Utilize the flow type (flow { ... })
    // TODO: Error and intermediate state handling
    // TODO
    operator fun invoke(prefix: String): List<Ingredient> =
        repository
            .searchRecipes(prefix)
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
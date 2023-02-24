package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.RecipeSearchRepository
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.network.model.NetworkIngredient
import javax.inject.Inject
import javax.inject.Provider

class GetRecipesUseCase @Inject constructor(
    private val repository: Provider<RecipeSearchRepository>
) : UseCase() {

    // TODO: Utilize the flow type (flow { ... })
    // TODO: Error and intermediate state handling
    // TODO
    operator fun invoke(prefix: String): List<Ingredient> =
        repository
            .get()
            .searchRecipes(prefix)
            .toExternalModel()

    private fun List<NetworkIngredient>.toExternalModel(): List<Ingredient> =
        this.map {
            Ingredient(
                id = it.id,
                name = it.name,
                image = it.image
            )
        }
}
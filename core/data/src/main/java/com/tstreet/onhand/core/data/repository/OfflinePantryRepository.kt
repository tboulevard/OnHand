package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.database.dao.IngredientCatalogDao
import com.tstreet.onhand.core.database.model.IngredientCatalogEntity
import com.tstreet.onhand.core.database.model.asExternalModel
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject
import javax.inject.Provider

class OfflinePantryRepository @Inject constructor(
    private val ingredientCatalogDao: Provider<IngredientCatalogDao>
) : PantryRepository {

    override suspend fun addIngredient(ingredient: Ingredient) {
        ingredientCatalogDao
            .get()
            .addToPantry(
                ingredientId = ingredient.id
            )
    }

    override suspend fun removeIngredient(ingredient: Ingredient) {
        ingredientCatalogDao
            .get()
            .removeFromPantry(
                ingredientId = ingredient.id
            )
    }

    override suspend fun listPantry(): List<Ingredient> {
        return ingredientCatalogDao
            .get()
            .getAllFromPantry()
            .map(IngredientCatalogEntity::asExternalModel)
    }
}

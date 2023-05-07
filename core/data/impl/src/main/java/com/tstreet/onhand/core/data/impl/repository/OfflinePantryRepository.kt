package com.tstreet.onhand.core.data.impl.repository

import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.database.dao.IngredientCatalogDao
import com.tstreet.onhand.core.database.model.IngredientCatalogEntity
import com.tstreet.onhand.core.database.model.asExternalModel
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.PantryIngredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class OfflinePantryRepository @Inject constructor(
    private val ingredientCatalogDao: Provider<IngredientCatalogDao>
) : PantryRepository {

    override suspend fun addIngredient(ingredient: Ingredient) : Int {
        return ingredientCatalogDao
            .get()
            .addToPantry(
                ingredientName = ingredient.name
            )
    }

    override suspend fun removeIngredient(ingredient: Ingredient) : Int {
        return ingredientCatalogDao
            .get()
            .removeFromPantry(
                ingredientName = ingredient.name
            )
    }

    override fun listPantry(): Flow<List<PantryIngredient>> {
        return ingredientCatalogDao
            .get()
            .getAllFromPantry()
            .map { it.map(IngredientCatalogEntity::asExternalModel) }
    }
}

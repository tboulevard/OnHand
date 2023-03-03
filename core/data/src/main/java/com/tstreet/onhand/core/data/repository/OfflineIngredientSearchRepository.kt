package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.database.dao.IngredientCatalogDao
import com.tstreet.onhand.core.database.model.IngredientCatalogEntity
import com.tstreet.onhand.core.database.model.asExternalModel
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject
import javax.inject.Provider

// TODO: refactor to offline first...
class OfflineIngredientSearchRepository @Inject constructor(
    private val ingredientCatalogDao: Provider<IngredientCatalogDao>
) : IngredientSearchRepository {
    // TODO: Refactor the fact that we're exposing NetworkIngredient to Domain layer

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    override suspend fun searchIngredients(prefix: String): List<Ingredient> {
        return ingredientCatalogDao.get()
            // TODO: revert
            .findByName(prefix)
            .map(IngredientCatalogEntity::asExternalModel)
    }
}

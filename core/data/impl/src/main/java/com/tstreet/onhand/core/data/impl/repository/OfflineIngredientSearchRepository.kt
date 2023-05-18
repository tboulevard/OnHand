package com.tstreet.onhand.core.data.impl.repository

import com.tstreet.onhand.core.data.api.repository.IngredientSearchRepository
import com.tstreet.onhand.core.database.dao.IngredientCatalogDao
import com.tstreet.onhand.core.database.model.IngredientCatalogEntity
import com.tstreet.onhand.core.database.model.asExternalModel
import com.tstreet.onhand.core.model.PantryIngredient
import javax.inject.Inject
import javax.inject.Provider

class OfflineIngredientSearchRepository @Inject constructor(
    // TODO: Despite this repository being a Singleton, IngredientCatalogDao created every time
    // TODO: we run a search query. Look into whether this is expected or if Dagger 2 setup is
    // TODO: wrong
    private val ingredientCatalogDao: Provider<IngredientCatalogDao>
) : IngredientSearchRepository {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    override suspend fun searchIngredients(query: String): List<PantryIngredient> {
        return ingredientCatalogDao.get()
            .search(query)
            .map(IngredientCatalogEntity::asExternalModel)
    }
}

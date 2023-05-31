package com.tstreet.onhand.core.data.impl.repository

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.data.api.repository.IngredientSearchRepository
import com.tstreet.onhand.core.database.dao.IngredientCatalogDao
import com.tstreet.onhand.core.database.model.IngredientCatalogEntity
import com.tstreet.onhand.core.database.model.asRecipePreview
import com.tstreet.onhand.core.model.PantryIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class OfflineIngredientSearchRepository @Inject constructor(
    private val ingredientCatalogDao: Provider<IngredientCatalogDao>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : IngredientSearchRepository {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    override fun searchIngredients(query: String): Flow<List<PantryIngredient>> {
        return ingredientCatalogDao.get()
            .search(query)
            .map { it.map(IngredientCatalogEntity::asRecipePreview) }
            .flowOn(ioDispatcher)

    }
}

package com.tstreet.onhand.core.data.impl.repository

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.database.dao.IngredientCatalogDao
import com.tstreet.onhand.core.database.model.IngredientCatalogEntity
import com.tstreet.onhand.core.database.model.asExternalModel
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.PantryIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class OfflinePantryRepository @Inject constructor(
    private val ingredientCatalogDao: Provider<IngredientCatalogDao>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : PantryRepository {

    override suspend fun addIngredient(ingredient: Ingredient): Int {
        return try {
            ingredientCatalogDao
                .get()
                .addToPantry(ingredient.name)
        } catch (e: Exception) {
            // TODO: rethrow in debug mode
            println("[OnHand] Error adding ingredient to pantry: ${e.message}")
            0 // For 0 rows affected
        }
    }

    override suspend fun removeIngredient(ingredient: Ingredient): Int {
        return try {
            ingredientCatalogDao
                .get()
                .removeFromPantry(ingredient.name)
        } catch (e: Exception) {
            // TODO: rethrow in debug mode
            println("[OnHand] Error removing ingredient from pantry: ${e.message}")
            0 // For 0 rows affected
        }
    }

    override fun listPantry(): Flow<List<PantryIngredient>> {
        return ingredientCatalogDao
            .get()
            .getAllFromPantry()
            .map { it.map(IngredientCatalogEntity::asExternalModel) }
            .flowOn(ioDispatcher)
    }
}

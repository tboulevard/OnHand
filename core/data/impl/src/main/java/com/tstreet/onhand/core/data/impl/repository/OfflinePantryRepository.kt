package com.tstreet.onhand.core.data.impl.repository

import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.database.dao.PantryDao
import com.tstreet.onhand.core.database.model.PantryEntity
import com.tstreet.onhand.core.database.model.toIngredient
import com.tstreet.onhand.core.database.model.toPantryEntity
import com.tstreet.onhand.core.model.Ingredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class OfflinePantryRepository @Inject constructor(
    private val pantryDao: Provider<PantryDao>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : PantryRepository {

    override suspend fun addIngredient(ingredient: Ingredient): Long {
        return withContext(ioDispatcher) {
            try {
                pantryDao
                    .get()
                    .addToPantry(ingredient.toPantryEntity())
            } catch (e: Exception) {
                // TODO: rethrow in debug mode
                Log.d("[OnHand]", "Error adding ingredient to pantry: ${e.message}")
                0 // For 0 rows affected
            }
        }
    }

    override suspend fun removeIngredient(ingredient: Ingredient): Int {
        return withContext(ioDispatcher) {
            try {
                pantryDao
                    .get()
                    .removeFromPantry(ingredient.toPantryEntity())
            } catch (e: Exception) {
                // TODO: rethrow in debug mode
                Log.d("[OnHand]", "Error removing ingredient from pantry: ${e.message}")
                0 // For 0 rows affected
            }
        }
    }

    override fun listPantry(): Flow<List<Ingredient>> {
        return pantryDao
            .get()
            .getAllFromPantry()
            .map {
                it.map(PantryEntity::toIngredient)
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun listPantry(ingredients: List<Ingredient>): List<Ingredient> {
        return pantryDao
            .get()
            .getPantryItemsWithIds(ingredients.map { it.id })
            .map { it.toIngredient() }
    }
}

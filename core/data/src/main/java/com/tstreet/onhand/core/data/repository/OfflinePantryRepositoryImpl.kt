package com.tstreet.onhand.core.data.repository

import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.database.dao.PantryDao
import com.tstreet.onhand.core.database.model.PantryEntity
import com.tstreet.onhand.core.database.model.toIngredient
import com.tstreet.onhand.core.database.model.toPantryEntity
import com.tstreet.onhand.core.model.data.Ingredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.collections.map

@Singleton
class OfflinePantryRepositoryImpl @Inject constructor(
    private val pantryDao: Provider<PantryDao>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : PantryRepository {

    init {
        Log.d("[OnHand]", "Creating ${this.javaClass.simpleName}")
    }

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

    override suspend fun listPantry(): List<Ingredient> {
        return withContext(ioDispatcher) {
            // Artificial delay to simulate loading
            delay((1000..2000L).random())

            pantryDao
                .get()
                .getAllFromPantry()
                .map(PantryEntity::toIngredient)
        }
    }

    override suspend fun listPantry(ingredients: List<Ingredient>): List<Ingredient> {
        return withContext(ioDispatcher) {
            pantryDao
                .get()
                .getPantryItemsWithIds(ingredients.map { it.id })
                .map { it.toIngredient() }
        }
    }
}

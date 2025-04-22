package com.tstreet.onhand.core.data.impl.repository

import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.data.api.repository.IngredientSearchRepository
import com.tstreet.onhand.core.database.dao.IngredientDao
import com.tstreet.onhand.core.database.model.IngredientEntity
import com.tstreet.onhand.core.database.model.toIngredient
import com.tstreet.onhand.core.model.data.Ingredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class OfflineIngredientSearchRepository @Inject constructor(
    private val dao: Provider<IngredientDao>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : IngredientSearchRepository {

    init {
        Log.d("[OnHand]", "Creating $TAG")
    }

    override fun searchIngredients(query: String): Flow<List<Ingredient>> {
        return dao.get()
            .getIngredients(query)
            .map {
                // Artificial delay to simulate loading
                delay((500L..1000L).random())
                it.map(IngredientEntity::toIngredient)
            }.flowOn(ioDispatcher)
    }
}

private val TAG = OfflineIngredientSearchRepository::class.simpleName

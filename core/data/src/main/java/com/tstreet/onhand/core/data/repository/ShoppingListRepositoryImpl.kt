package com.tstreet.onhand.core.data.impl.repository

import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.database.dao.ShoppingListDao
import com.tstreet.onhand.core.database.model.*
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.ShoppingListIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ShoppingListRepositoryImpl @Inject constructor(
    private val shoppingListDao: Provider<ShoppingListDao>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : ShoppingListRepository {

    init {
        Log.d("[OnHand]", "Creating ${this.javaClass.simpleName}")
    }

    override fun getShoppingList(): Flow<Resource<List<ShoppingListIngredient>>> {
        return shoppingListDao
            .get()
            .getShoppingList()
            .map {
                Resource.success(
                    it.map(ShoppingListEntity::toExternalModel)
                )
            }
            .catch {
                // TODO: rethrow in debug mode
                Log.d("[OnHand]", "Error retrieving shopping list ingredients: ${it.message}")
                // In the context of a FlowCollector, so we need to emit
                emit(Resource.error<Nothing>(msg = it.message.toString()))
            }
            .flowOn(ioDispatcher)
    }

    override fun getRecipesInShoppingList(): Flow<Resource<List<RecipePreview>>> {
        return shoppingListDao
            .get()
            .getRecipesInShoppingList()
            .map { recipes ->
                Resource.success(
                    // SQL statement should guarantee non-null - .mapNotNull for compile safety
                    recipes.mapNotNull { it }
                )
            }
            .catch {
                // TODO: rethrow in debug mode
                Log.d("[OnHand]", "Error retrieving shopping list recipes: ${it.message}")
                // In the context of a FlowCollector, so we need to emit
                emit(Resource.error<Nothing>(msg = it.message.toString()))
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun insertIngredients(
        shoppingList: List<ShoppingListIngredient>
    ): Resource<Unit> {
        return try {
            shoppingListDao
                .get()
                .insertShoppingList(shoppingList.map(ShoppingListIngredient::asEntity))
            Resource.success(null)
        } catch (e: Exception) {
            // TODO: rethrow in debug
            Resource.error(msg = e.message.toString())
        }
    }

    override suspend fun checkOffIngredient(ingredient: ShoppingListIngredient): Resource<Unit> {
        return try {
            shoppingListDao
                .get()
                .markIngredientPurchased(ingredient.name)
            Resource.success(null)
        } catch (e: Exception) {
            // TODO: rethrow in debug
            Resource.error(msg = e.message.toString())
        }
    }

    override suspend fun uncheckIngredient(ingredient: ShoppingListIngredient): Resource<Unit> {
        return try {
            shoppingListDao
                .get()
                .unmarkIngredientPurchased(ingredient.name)
            Resource.success(null)
        } catch (e: Exception) {
            // TODO: rethrow in debug
            Resource.error(msg = e.message.toString())
        }
    }

    override suspend fun isIngredientCheckedOff(name: String): Boolean {
        return shoppingListDao
            .get()
            .isShoppingListIngredientPurchased(name)
    }

    override suspend fun removeRecipePreview(recipePreview: RecipePreview): Resource<Unit> {
        return try {
            shoppingListDao.get().removeRecipePreview(recipePreview)
            Resource.success(null)
        } catch (e: Exception) {
            // TODO: rethrow in debug
            Resource.error(msg = e.message.toString())
        }
    }

    override suspend fun isEmpty(): Boolean {
        return shoppingListDao
            .get()
            .isEmpty()
    }

    override suspend fun removeIngredient(ingredient: ShoppingListIngredient): Resource<Unit> {
        return withContext(ioDispatcher) {
            try {
                shoppingListDao
                    .get()
                    .removeIngredient(ingredient.name)
                Resource.success(null)
            } catch (e: Exception) {
                // TODO: rethrow in debug
                Log.d("[OnHand]", "Error removing $ingredient from Shopping List, msg=${e.message}")
                Resource.error(msg = e.message.toString())
            }
        }
    }
}

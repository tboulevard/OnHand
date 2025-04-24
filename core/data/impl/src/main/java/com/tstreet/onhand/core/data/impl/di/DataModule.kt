package com.tstreet.onhand.core.data.impl.di

import com.tstreet.onhand.core.domain.repository.IngredientSearchRepository
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.domain.repository.RecipeRepository
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.data.impl.repository.OfflineIngredientSearchRepositoryImpl
import com.tstreet.onhand.core.data.impl.repository.OfflinePantryRepositoryImpl
import com.tstreet.onhand.core.data.impl.repository.RecipeRepositoryImpl
import com.tstreet.onhand.core.data.impl.repository.ShoppingListRepositoryImpl
import com.tstreet.onhand.core.database.DaosModule
import com.tstreet.onhand.core.database.DatabaseModule
import com.tstreet.onhand.core.network.di.NetworkModule
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(
    includes = [
        NetworkModule::class,
        DatabaseModule::class,
        DaosModule::class
    ]
)
interface DataModule {

    @Binds
    // TODO: scope annotations at the module level seems to do nothing and are only
    //  needed at the class level? Look into later. For @Provides methods, class level
    //  scope annotations do nothing - you need to annotate on the @Provides methods
    fun bindsIngredientSearchRepository(
        ingredientSearchRepository: OfflineIngredientSearchRepositoryImpl
    ): IngredientSearchRepository

    @Binds
    // TODO: scope annotations at the module level seems to do nothing and are only
    //  needed at the class level? Look into later
    fun bindsRecipeSearchRepository(
        recipeSearchRepository: RecipeRepositoryImpl
    ): RecipeRepository

    @Binds
    // TODO: scope annotations at the module level seems to do nothing and are only
    //  needed at the class level? Look into later
    fun bindsPantryRepository(
        pantryRepository: OfflinePantryRepositoryImpl
    ): PantryRepository

    @Binds
    // TODO: scope annotations at the module level seems to do nothing and are only
    //  needed at the class level? Look into later
    fun bindsShoppingListRepository(
        shoppingListRepository: ShoppingListRepositoryImpl
    ): ShoppingListRepository
}

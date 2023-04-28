package com.tstreet.onhand.core.data.impl

import com.tstreet.onhand.core.data.api.repository.IngredientSearchRepository
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository
import com.tstreet.onhand.core.data.impl.repository.OfflineIngredientSearchRepository
import com.tstreet.onhand.core.data.impl.repository.OfflinePantryRepository
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
    @Singleton
    fun bindsIngredientSearchRepository(
        ingredientSearchRepository: OfflineIngredientSearchRepository
    ): IngredientSearchRepository

    @Binds
    // TODO: scope annotations at the module level seems to do nothing and are only
    //  needed at the class level? Look into later
    @Singleton
    fun bindsRecipeSearchRepository(
        recipeSearchRepository: RecipeRepositoryImpl
    ): RecipeRepository

    @Binds
    // TODO: scope annotations at the module level seems to do nothing and are only
    //  needed at the class level? Look into later
    @Singleton
    fun bindsPantryRepository(
        pantryRepository: OfflinePantryRepository
    ): PantryRepository

    @Binds
    // TODO: scope annotations at the module level seems to do nothing and are only
    //  needed at the class level? Look into later
    @Singleton
    fun bindsShoppingListRepository(
        shoppingListRepository: ShoppingListRepositoryImpl
    ): ShoppingListRepository
}

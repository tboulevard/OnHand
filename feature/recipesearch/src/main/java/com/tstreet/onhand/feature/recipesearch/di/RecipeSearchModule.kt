package com.tstreet.onhand.feature.recipesearch.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.recipes.GetRecipesUseCase
import com.tstreet.onhand.core.domain.recipes.SaveRecipeUseCase
import com.tstreet.onhand.core.domain.recipes.UnsaveRecipeUseCase
import com.tstreet.onhand.core.domain.shoppinglist.AddToShoppingListUseCase
import dagger.Binds
import dagger.Module

@Module
interface RecipeSearchModule {

    @Binds
    fun bindsGetRecipesUseCase(
        useCase: GetRecipesUseCase
    ): UseCase

    @Binds
    fun bindsSaveRecipesUseCase(
        useCase: SaveRecipeUseCase
    ): UseCase

    @Binds
    fun bindsUnsaveRecipesUseCase(
        useCase: UnsaveRecipeUseCase
    ): UseCase

    @Binds
    fun bindsAddToShoppingListUseCase(
        useCase: AddToShoppingListUseCase
    ): UseCase
}

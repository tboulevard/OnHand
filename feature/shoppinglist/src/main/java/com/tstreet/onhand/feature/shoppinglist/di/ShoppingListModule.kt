package com.tstreet.onhand.feature.shoppinglist.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.GetShoppingListUseCase
import com.tstreet.onhand.core.domain.MarkShoppingIngredientUseCase
import com.tstreet.onhand.core.domain.UnmarkShoppingIngredientUseCase
import dagger.Binds
import dagger.Module

@Module
interface ShoppingListModule {

    @Binds
    fun bindsGetRecipesUseCase(
        useCase: GetShoppingListUseCase
    ): UseCase

    @Binds
    fun bindsMarkShoppingListIngredientUseCase(
        useCase: MarkShoppingIngredientUseCase
    ): UseCase

    @Binds
    fun bindsunmarkShoppingListIngredientUseCase(
        useCase: UnmarkShoppingIngredientUseCase
    ): UseCase
}

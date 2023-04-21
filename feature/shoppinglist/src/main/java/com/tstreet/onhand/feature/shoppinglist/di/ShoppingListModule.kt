package com.tstreet.onhand.feature.shoppinglist.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.shoppinglist.GetShoppingListUseCase
import com.tstreet.onhand.core.domain.shoppinglist.CheckOffIngredientUseCase
import com.tstreet.onhand.core.domain.shoppinglist.UncheckIngredientUseCase
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
        useCase: CheckOffIngredientUseCase
    ): UseCase

    @Binds
    fun bindsunmarkShoppingListIngredientUseCase(
        useCase: UncheckIngredientUseCase
    ): UseCase
}

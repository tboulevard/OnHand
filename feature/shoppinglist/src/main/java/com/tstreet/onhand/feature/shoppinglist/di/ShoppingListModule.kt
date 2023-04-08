package com.tstreet.onhand.feature.shoppinglist.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.GetShoppingListUseCase
import dagger.Binds
import dagger.Module

@Module
interface ShoppingListModule {

    @Binds
    fun bindsGetRecipesUseCase(
        useCase: GetShoppingListUseCase
    ): UseCase
}
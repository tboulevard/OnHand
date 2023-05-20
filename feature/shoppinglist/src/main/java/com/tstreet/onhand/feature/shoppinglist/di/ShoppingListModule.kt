package com.tstreet.onhand.feature.shoppinglist.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.shoppinglist.*
import dagger.Binds
import dagger.Module

@Module
interface ShoppingListModule {

    @Binds
    fun bindsGetRecipesUseCase(
        useCase: GetShoppingListUseCase
    ): UseCase

    @Binds
    fun bindsCheckOffIngredientUseCase(
        useCase: CheckOffIngredientUseCase
    ): UseCase

    @Binds
    fun bindsUncheckIngredientUseCase(
        useCase: UncheckIngredientUseCase
    ): UseCase

    @Binds
    fun bindsGetRecipesInShoppingListUseCase(
        useCase: GetRecipesInShoppingListUseCase
    ): UseCase

    @Binds
    fun bindsRemoveRecipeInShoppingListUseCase(
        useCase: RemoveRecipeInShoppingListUseCase
    ): UseCase

    @Binds
    fun bindsRemoveIngredientInShoppingListUseCase(
        useCase: RemoveIngredientUseCase
    ): UseCase
}

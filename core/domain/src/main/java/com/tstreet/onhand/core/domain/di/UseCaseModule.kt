package com.tstreet.onhand.core.domain.di

import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.usecase.ingredients.GetIngredientsUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.RemoveFromPantryUseCase
import com.tstreet.onhand.core.domain.usecase.recipes.GetRecipesUseCase
import com.tstreet.onhand.core.domain.usecase.recipes.GetSavedRecipesUseCase
import com.tstreet.onhand.core.domain.usecase.recipes.SaveRecipeUseCase
import com.tstreet.onhand.core.domain.usecase.recipes.UnsaveRecipeUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.AddToShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.CheckOffIngredientUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.GetRecipesInShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.GetShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.RemoveIngredientUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.RemoveRecipeInShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.UncheckIngredientUseCase
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface UseCaseModule {

    @Binds
    @Singleton
    fun bindsGetIngredientsUseCase(
        useCase: GetIngredientsUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsAddToPantryUseCase(
        useCase: AddToPantryUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsRemoveFromPantryUseCase(
        useCase: RemoveFromPantryUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsGetRecipesUseCase(
        useCase: GetRecipesUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsSaveRecipesUseCase(
        useCase: SaveRecipeUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsUnsaveRecipesUseCase(
        useCase: UnsaveRecipeUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsAddToShoppingListUseCase(
        useCase: AddToShoppingListUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsCheckOffIngredientUseCase(
        useCase: CheckOffIngredientUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsUncheckIngredientUseCase(
        useCase: UncheckIngredientUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsGetRecipesInShoppingListUseCase(
        useCase: GetRecipesInShoppingListUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsRemoveRecipeInShoppingListUseCase(
        useCase: RemoveRecipeInShoppingListUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsRemoveIngredientInShoppingListUseCase(
        useCase: RemoveIngredientUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsGetSavedRecipeUseCase(
        useCase: GetSavedRecipesUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsGetShoppingListUseCase(
        useCase: GetShoppingListUseCase
    ): UseCase
}
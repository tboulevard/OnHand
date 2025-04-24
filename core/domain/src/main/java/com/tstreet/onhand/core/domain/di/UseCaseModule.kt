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

@Module
interface UseCaseModule {

    @Binds
    fun bindsGetIngredientsUseCase(
        useCase: GetIngredientsUseCase
    ): UseCase

    @Binds
    fun bindsAddToPantryUseCase(
        useCase: AddToPantryUseCase
    ): UseCase

    @Binds
    fun bindsRemoveFromPantryUseCase(
        useCase: RemoveFromPantryUseCase
    ): UseCase

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

    @Binds
    fun bindsGetSavedRecipeUseCase(
        useCase: GetSavedRecipesUseCase
    ): UseCase

    @Binds
    fun bindsGetShoppingListUseCase(
        useCase: GetShoppingListUseCase
    ): UseCase
}
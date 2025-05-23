package com.tstreet.onhand.core.data.di

import com.tstreet.onhand.core.common.CommonComponent
import com.tstreet.onhand.core.domain.repository.IngredientSearchRepository
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.domain.repository.RecipeRepository
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [
        CommonComponent::class
    ],
    modules = [
        DataModule::class
    ]
)
interface DataComponent {

    val ingredientSearchRepository: IngredientSearchRepository
    val recipeRepository: RecipeRepository
    val pantryRepository: PantryRepository
    val shoppingListRepository: ShoppingListRepository
}
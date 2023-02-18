package com.tstreet.onhand.core.data.di

import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.core.data.repository.RecipeSearchRepository
import dagger.Component
import javax.inject.Singleton

// Operates like @Singleton scope, but bc dagger doesn't let @Single components depend on scoped
// components we need to do this...
// TODO: why though?
@Singleton
@Component(
    modules = [
        DataModule::class
    ]
)
interface DataComponent : DataComponentProvider
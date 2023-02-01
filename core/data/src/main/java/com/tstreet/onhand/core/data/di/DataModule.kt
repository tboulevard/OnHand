package com.tstreet.onhand.core.data.di

import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.core.data.repository.OnlineFirstIngredientSearchRepository
import dagger.Module

@Module
interface DataModule {

    fun IngredientSearchRepository.binds() : OnlineFirstIngredientSearchRepository
}
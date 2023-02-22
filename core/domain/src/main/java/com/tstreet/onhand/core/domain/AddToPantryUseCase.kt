package com.tstreet.onhand.core.domain

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.data.repository.PantryRepository
import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject
import javax.inject.Provider

class AddToPantryUseCase @Inject constructor(
    private val repository: Provider<PantryRepository>
) : UseCase() {

    init {
        println("Creating ${this.javaClass.simpleName}")
    }

    operator fun invoke(ingredient: Ingredient) {
        repository
            .get()
            .addIngredient(ingredient)
    }
}
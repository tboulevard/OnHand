package com.tstreet.onhand.core.domain.recipes

import com.tstreet.onhand.core.common.*
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.domain.recipes.SortBy.*
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.SaveableRecipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

// TODO: for some reason this scope isn't needed to bind use case to view lifecycle...is it
//  because [RecipeResultComponent] already specifies this? Either way, keeping
//  here to be pedantic...
// TODO: Also - Is it correct to have this annotation at the class or module level? For all
//  use cases, class level appears to work while module level does not
/**
 * Retrieves an individual [Recipe] based on its identifier.
 */
@FeatureScope
class GetRecipeUseCase @Inject constructor(
    private val recipeRepository: Provider<RecipeRepository>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase() {

    operator fun invoke(id: Int): Flow<Resource<Recipe>> {
        // TODO: revisit, doesn't need to be a flow.
        return flow { emit(recipeRepository.get().getRecipe(id)) }.flowOn(ioDispatcher)
    }
}

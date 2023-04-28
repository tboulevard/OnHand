package com.tstreet.onhand.feature.recipedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.domain.recipes.GetRecipeDetailUseCase
import com.tstreet.onhand.core.ui.RecipeDetailUiState
import com.tstreet.onhand.feature.recipedetail.di.RecipeId
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Provider

class RecipeDetailViewModel @Inject constructor(
    getRecipeDetail: Provider<GetRecipeDetailUseCase>,
    @RecipeId private val recipeId: Int,
) : ViewModel() {

    // TODO: Need to handle state if default recipeId (0) is passed through. Null recipeId theoretically
    // not possible, but we allow it because of how passing navArgument works. This prevents crashes - recipeId = 0
    // will just link to an invalid url and show nothing.
    val recipeDetailUiState =
        when (recipeId) {
            INVALID_RECIPE_ID -> {
                MutableStateFlow(
                    RecipeDetailUiState.Error(
                        message = "Error: Received null id for detail"
                    )
                )
            }
            else -> {
                getRecipeDetail.get().invoke(recipeId)
                    .map(RecipeDetailUiState::Success)
                    .stateIn(
                        // TODO: revisit scoping since we're doing network operations behind the scenes
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = RecipeDetailUiState.Loading
                    )
            }
        }
}

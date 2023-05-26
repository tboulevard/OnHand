package com.tstreet.onhand.feature.recipedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.domain.recipes.GetRecipeDetailUseCase
import com.tstreet.onhand.core.ui.RecipeDetailUiState
import com.tstreet.onhand.feature.recipedetail.di.IsCustomRecipe
import com.tstreet.onhand.feature.recipedetail.di.RecipeId
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Provider

class RecipeDetailViewModel @Inject constructor(
    getRecipeDetail: Provider<GetRecipeDetailUseCase>,
    @RecipeId private val recipeId: Int,
    @IsCustomRecipe private val isCustom: Boolean
) : ViewModel() {

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog = _showErrorDialog
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _showErrorDialog.value
        )

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
                // TODO: flow isn't really needed here, but for MVP keep this...
                getRecipeDetail.get().invoke(
                    recipeId,
                    isCustom
                )
                    .map {
                        when (it.status) {
                            SUCCESS -> {
                                // TODO: handle null
                                RecipeDetailUiState.Success(it.data!!)
                            }
                            ERROR -> {
                                _showErrorDialog.update { true }
                                RecipeDetailUiState.Error(it.message.toString())
                            }
                        }
                    }
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = RecipeDetailUiState.Loading
                    )
            }
        }

    fun dismissErrorDialog() {
        _showErrorDialog.update { false }
    }
}

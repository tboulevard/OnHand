package com.tstreet.onhand.feature.recipedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.domain.recipes.GetFullRecipeUseCase
import com.tstreet.onhand.core.ui.RecipeDetailUiState
import com.tstreet.onhand.feature.recipedetail.di.RecipeId
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Provider

class RecipeDetailViewModel @Inject constructor(
    getFullRecipe: Provider<GetFullRecipeUseCase>,
    @RecipeId private val recipeId: Int?,
) : ViewModel() {

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog = _showErrorDialog
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _showErrorDialog.value
        )

    val recipeDetailUiState =
        when (recipeId) {
            null -> {
                MutableStateFlow(
                    RecipeDetailUiState.Error(
                        message = "Error: Received null id for recipe, cannot fetch detail."
                    )
                )
            }
            else -> {
                getFullRecipe.get().invoke(
                    recipeId
                ).map {
                    when (it.status) {
                        SUCCESS -> {
                            RecipeDetailUiState.Success(
                                recipePreview = it.data?.preview,
                                detail = it.data?.detail
                            )
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

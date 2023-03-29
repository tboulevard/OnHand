package com.tstreet.onhand.feature.recipedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.domain.GetRecipeDetailUseCase
import com.tstreet.onhand.core.model.RecipeDetail
import com.tstreet.onhand.feature.recipedetail.di.RecipeId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class RecipeDetailViewModel @Inject constructor(
    getRecipeDetail: Provider<GetRecipeDetailUseCase>,
    @RecipeId private val recipeId: Int,
    // TODO: leaving around as an example...
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    // Once we receive the recipe id, then invoke? Waiting up to x seconds?

    // TODO: error handling around null recipe id instead of default to 0
    val recipeDetailUiState = getRecipeDetail.get().invoke(recipeId)
        .map(RecipeDetailUiState::Success)
        .stateIn(
            // TODO: revisit scoping since we're doing database operations behind the scenes
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RecipeDetailUiState.Loading
        )
}

sealed interface RecipeDetailUiState {

    object Loading : RecipeDetailUiState

    data class Success(
        val recipeDetail: RecipeDetail
    ) : RecipeDetailUiState

    object Error : RecipeDetailUiState
}

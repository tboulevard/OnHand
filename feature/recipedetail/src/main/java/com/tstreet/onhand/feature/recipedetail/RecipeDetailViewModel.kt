package com.tstreet.onhand.feature.recipedetail

import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.ViewModelWithBundle
import com.tstreet.onhand.core.domain.GetRecipeDetailUseCase
import com.tstreet.onhand.core.model.RecipeDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class RecipeDetailViewModel @Inject constructor(
    private val getRecipeDetail: Provider<GetRecipeDetailUseCase>,
    // TODO: leaving around as an example...
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModelWithBundle() {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
        println("[OnHand] RecipeDetailViewModel bundle:" + bundle?.toString())
        println("[OnHand] This viewModel instance (viewModel src):$this")
    }

    // TODO: error handling around null recipe id instead of default to 0
    fun recipeDetailUiState() : StateFlow<RecipeDetailUiState> {
        println("[OnHand] Invoking recipeDetailUiState.collectState() with bundle, 1: "
                + bundle?.getBundle("recipeId").toString() + " 2: " + bundle?.getInt("recipeId") +
                "3: " + bundle?.getString("recipeId")
        )
        return getRecipeDetail.get().invoke(bundle?.getInt("recipeId") ?: 0)
            .map(RecipeDetailUiState::Success)
            .stateIn(
                // TODO: revisit scoping since we're doing database operations behind the scenes
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RecipeDetailUiState.Loading
            )
    }
}

sealed interface RecipeDetailUiState {

    object Loading : RecipeDetailUiState

    data class Success(
        val recipeDetail: RecipeDetail
    ) : RecipeDetailUiState

    object Error : RecipeDetailUiState
}

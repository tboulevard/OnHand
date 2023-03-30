package com.tstreet.onhand.feature.recipesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.domain.GetRecipesUseCase
import com.tstreet.onhand.core.ui.RecipeSearchUiState
import com.tstreet.onhand.feature.recipesearch.SortBy.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class RecipeSearchViewModel @Inject constructor(
    getRecipes: Provider<GetRecipesUseCase>,
    // TODO: leaving around as an example...
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    private val _sortOrder = MutableStateFlow(DEFAULT_SORT_ORDER)
    val recipeSearchUiState: StateFlow<RecipeSearchUiState> = _sortOrder
        .onEach { RecipeSearchUiState.Loading }
            // TODO: Each time we invoke use case it makes a network call - cache values on recipe
            //  screen in repo layer (only re-query network if pantry state has changed)
        .combine(getRecipes.get().invoke()) { sortBy, recipes ->
            // TODO: move logic into usecase layer for better separation of concerns. Leaving
            // here for v1
            when (sortBy) {
                POPULARITY -> recipes.sortedByDescending { it.likes }
                MISSING_INGREDIENTS -> recipes.sortedBy { it.missedIngredientCount }
            }
        }
        .map(RecipeSearchUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecipeSearchUiState.Loading
        )

    fun onRecipeSaved(index: Int) {
        /* TODO: implement */
    }

    fun onSortOrderChanged(sortingOrder: SortBy) {
        _sortOrder.update { sortingOrder }
    }
}

enum class SortBy {
    POPULARITY,
    MISSING_INGREDIENTS
}

val DEFAULT_SORT_ORDER = POPULARITY

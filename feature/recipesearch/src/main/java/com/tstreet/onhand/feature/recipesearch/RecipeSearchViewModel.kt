package com.tstreet.onhand.feature.recipesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.domain.GetRecipesUseCase
import com.tstreet.onhand.core.domain.SaveRecipeUseCase
import com.tstreet.onhand.core.ui.RecipeSearchUiState
import com.tstreet.onhand.feature.recipesearch.SortBy.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class RecipeSearchViewModel @Inject constructor(
    getRecipes: Provider<GetRecipesUseCase>,
    saveRecipes : Provider<SaveRecipeUseCase>,
    // TODO: leaving around as an example...
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    private val _sortOrder = MutableStateFlow(DEFAULT_SORT_ORDER)
    val recipeSearchUiState: StateFlow<RecipeSearchUiState> = _sortOrder
        // TODO: note this onEach { } block currently does nothing because we don't actually emit
        // the value (instead initialValue on stateIn is obeyed). May need to look into SharedFlows
        // to re-trigger loading state on UI each time we change sort order (if desired)
        .onEach { RecipeSearchUiState.Loading }
        // Note: When we change the sort order, we don't re-invoke getRecipes use case
        .combine(getRecipes.get().invoke()) { sortBy, recipes ->
            // TODO: Potentially move logic into usecase layer for better separation of concerns
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

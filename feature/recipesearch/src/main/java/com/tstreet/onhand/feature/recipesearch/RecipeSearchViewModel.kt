package com.tstreet.onhand.feature.recipesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.domain.GetRecipesUseCase
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.feature.recipesearch.SortBy.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    private val _sortBy = MutableStateFlow(POPULARITY)
    val recipes: StateFlow<List<Recipe>> = _sortBy
        .onEach { _isLoading.update { true } }
            // TODO: Each time we invoke use case it makes a network call - cache values on recipe screen in repo layer
        .combine(getRecipes.get().invoke()) { sortBy, recipes ->
            // TODO: move logic into usecase layer
            when (sortBy) {
                POPULARITY -> recipes.sortedByDescending { it.likes }
                MISSING_INGREDIENTS -> recipes.sortedBy { it.missedIngredientCount }
            }
        }
        .onEach { _isLoading.update { false } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onRecipeSaved(index: Int) {
        /* TODO: implement */
    }

    fun onSortingOrderChanged(sortingOrder: SortBy) {
        _sortBy.update { sortingOrder }
    }
}

enum class SortBy {
    POPULARITY,
    MISSING_INGREDIENTS
}

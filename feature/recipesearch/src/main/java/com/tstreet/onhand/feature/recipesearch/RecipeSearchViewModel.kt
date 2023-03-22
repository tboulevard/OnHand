package com.tstreet.onhand.feature.recipesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.domain.GetRecipesUseCase
import com.tstreet.onhand.core.model.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class RecipeSearchViewModel @Inject constructor(
    private val getRecipes: Provider<GetRecipesUseCase>,
    // TODO: leaving around as an example...
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching

    val recipes: StateFlow<List<Recipe>> =
        flow { emit(getRecipes.get().invoke()) }
            .onStart { _isSearching.update { true } }
            .onCompletion { _isSearching.update { false } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun onRecipeClicked(index: Int) {
        /* TODO: implement */
    }

    fun onRecipeSaved(index: Int) {
        /* TODO: implement */
    }
}

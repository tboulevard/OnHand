package com.tstreet.onhand.feature.recipesearch

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule
import com.tstreet.onhand.core.domain.GetRecipesUseCase
import com.tstreet.onhand.core.model.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class RecipeSearchViewModel @Inject constructor(
    private val getRecipes: Provider<GetRecipesUseCase>,
    // TODO: leaving around as an example...
    @Named(CommonModule.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _isSearching: MutableStateFlow<Boolean>
    var isSearching: StateFlow<Boolean>
    var recipes: SnapshotStateList<Recipe>

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
        // TODO: cleanup, should have a different trigger site
        _isSearching = MutableStateFlow(false)
        isSearching = _isSearching
        recipes = mutableStateListOf()
        search()
    }

    fun onRecipeClicked(index: Int) {

    }

    private fun search() {
        viewModelScope.launch {
            // Runs on UI/main thread
            _isSearching.update { true }

            // Run on a different dispatcher
            // TODO fix before merge: this is causing intermittent crashes:
            // java.lang.IllegalStateException: Reading a state that was created after the snapshot was taken or in a snapshot that has not yet been applied
            withContext(ioDispatcher) {
                recipes.addAll(getRecipes.get().invoke().toMutableStateList())
            }

            // Runs on UI/main thread
            _isSearching.update { false }
        }
    }
}
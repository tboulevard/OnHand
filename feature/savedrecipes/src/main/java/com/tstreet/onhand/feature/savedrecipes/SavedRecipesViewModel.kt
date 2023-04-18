package com.tstreet.onhand.feature.savedrecipes

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.domain.GetSavedRecipesUseCase
import com.tstreet.onhand.core.domain.SaveRecipeUseCase
import com.tstreet.onhand.core.domain.UnsaveRecipeUseCase
import com.tstreet.onhand.core.ui.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class SavedRecipesViewModel @Inject constructor(
    getSavedRecipes: Provider<GetSavedRecipesUseCase>,
    private val unsaveRecipe: Provider<UnsaveRecipeUseCase>,
    private val saveRecipe: Provider<SaveRecipeUseCase>
) : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    private var _recipes = mutableStateListOf<RecipeWithSaveState>()

    val uiState = getSavedRecipes
        .get()
        .invoke()
        .map {
            _recipes = it.toRecipeWithSaveStateItemList()
            // TODO NOTE: By passing this here and making mutations on _recipes in this class, we
            //  trigger a recomposition each time. This is causing uiState to retrigger (and call
            //  getSavedRecipes each time an item is added/removed from the list.
            _recipes
        }
        .map(SavedRecipesUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SavedRecipesUiState.Loading
        )

    fun onRecipeSaved(index: Int) {
        // TODO: wrap all this in a lock to prevent concurrent execution. in general make
        //  mutable states visible to only one thread
        viewModelScope.launch {
            val item = _recipes[index]
            val saveState = item.recipeSaveState
            // Mark the recipe as saving
            _recipes[index] = item.copy(recipeSaveState = RecipeSaveState.SAVING)
            // Save the recipe
            saveRecipe.get().invoke(item.recipe).collect {
                when (it) {
                    // When save is successful, update UI state
                    true -> {
                        _recipes[index] = item.copy(
                            recipeSaveState = RecipeSaveState.SAVED
                        )
                    }
                    else -> {
                        // TODO: todo better error handling
                        println(
                            "[OnHand] Recipe save unsuccessful, there was an exception - " +
                                    "recipe not saved"
                        )
                        // Retain the previous save state on error
                        _recipes[index] = item.copy(
                            recipeSaveState = saveState
                        )
                    }
                }
            }
        }
    }

    fun onRecipeUnsaved(index: Int) {
        viewModelScope.launch {
            val item = _recipes[index]
            // Just unsave the recipe - no loading indicator
            unsaveRecipe.get().invoke(item.recipe).collect {
                when (it) {
                    // When the unsave is successful, update UI state
                    true -> {
                        _recipes[index] = item.copy(
                            recipeSaveState = RecipeSaveState.NOT_SAVED
                        )
                    }
                    else -> {
                        // TODO: todo better error handling
                        println(
                            "[OnHand] Recipe unsave unsuccessful, there was an exception - " +
                                    "recipe not removed from DB"
                        )
                    }
                }
            }
        }
    }
}

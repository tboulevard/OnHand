package com.tstreet.onhand.feature.customrecipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.domain.customrecipe.AddRecipeUseCase
import com.tstreet.onhand.core.model.PartialRecipe
import com.tstreet.onhand.core.model.RecipeIngredient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class CreateCustomRecipeViewModel @Inject constructor(
    private val addRecipeUseCase: Provider<AddRecipeUseCase>,
) : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    // required
    private val _recipeTitle = MutableStateFlow("")
    val recipeTitle = _recipeTitle.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _recipeTitle.value
    )

    // required = at least 1
    private val _ingredients = MutableStateFlow(emptyList<RecipeIngredient>())
    val ingredients = _ingredients.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _ingredients.value
    )

    // optional - address in future PR
    private val _coverImage = MutableStateFlow("")
    val coverImage = _coverImage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _coverImage.value
    )

    // optional
    private val _instructions = MutableStateFlow("")
    val instructions = _instructions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _instructions.value
    )

    fun onDoneClicked() {
        viewModelScope.launch {
            addRecipeUseCase.get().invoke(createPartialRecipe()).collect {
                println("[OnHand] Save recipe status=${it.status}")
            }
        }
    }

    fun onTitleChanged(text: String) {
        _recipeTitle.update { text }
    }

    fun onImageChanged(text: String) {
        TODO()
    }

    fun onIngredientsChanged(text: String) {
        TODO()
    }

    private fun createPartialRecipe() = PartialRecipe(
        recipeTitle = _recipeTitle.value,
        recipeInstructions = _instructions.value,
        ingredients = _ingredients.value,
        // TODO: revisit below when we allow submitting custom images
        recipeImage = _coverImage.value,
        recipeImageType = "",
    )
}

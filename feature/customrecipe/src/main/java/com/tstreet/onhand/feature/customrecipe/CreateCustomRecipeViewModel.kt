package com.tstreet.onhand.feature.customrecipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.domain.customrecipe.AddRecipeUseCase
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.PartialRecipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    private val _ingredients = MutableStateFlow(emptyList<Ingredient>())
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
            val result = addRecipeUseCase.get().invoke(createPartialRecipe())
        }
    }

    fun onTitleChanged(text: String) {
        _recipeTitle.update { text }
    }

    fun onImageChanged(text: String) {

    }

    fun onIngredientsChanged(text: String) {

    }

    private fun createPartialRecipe() = PartialRecipe(
        _recipeTitle.value,
        // TODO: when we allow submitting custom images
        _coverImage.value,
        _coverImage.value + ".jpg",
        _instructions.value

    )
}

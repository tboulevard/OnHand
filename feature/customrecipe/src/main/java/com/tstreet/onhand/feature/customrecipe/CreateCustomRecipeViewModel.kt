package com.tstreet.onhand.feature.customrecipe

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.domain.customrecipe.AddRecipeUseCase
import com.tstreet.onhand.core.model.CustomRecipeInput
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
    private val _title = MutableStateFlow("")
    val title = _title.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _title.value
    )

    // required - at least 1
    private val _ingredients = mutableStateListOf<RecipeIngredient>()
    val ingredients: List<RecipeIngredient> = _ingredients

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

    fun onTitleChanged(text: String) {
        _title.update { text }
    }

    fun onReceiveIngredients(ingredients: List<RecipeIngredient>) {
        // In case the user wants to add more ingredients after adding some previously
        _ingredients += ingredients
    }

    fun onRemoveIngredient(index: Int) {
        _ingredients -= _ingredients[index]
    }

    fun onInstructionsChanged(text: String) {
        _instructions.update { text }
    }

    fun onImageChanged(text: String) {
        // TODO
    }

    fun onDoneClicked() {
        viewModelScope.launch {
            addRecipeUseCase.get().invoke(createPartialRecipe()).collect {
                println("[OnHand] Save recipe status=${it.status}")
            }
        }
    }

    private fun createPartialRecipe() = CustomRecipeInput(
        recipeTitle = _title.value,
        instructions = _instructions.value,
        ingredients = _ingredients,
        // TODO: revisit below when we allow submitting custom images
        recipeImage = _coverImage.value,
        recipeImageType = "",
    )
}

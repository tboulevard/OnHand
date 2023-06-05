package com.tstreet.onhand.feature.customrecipe

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.Status.*
import com.tstreet.onhand.core.domain.customrecipe.AddRecipeUseCase
import com.tstreet.onhand.core.domain.customrecipe.ValidateCustomRecipeInputUseCase
import com.tstreet.onhand.core.model.CustomRecipeInput
import com.tstreet.onhand.core.model.RecipeIngredient
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class CreateCustomRecipeViewModel @Inject constructor(
    private val addRecipeUseCase: Provider<AddRecipeUseCase>,
    private val validateInputUseCase: Provider<ValidateCustomRecipeInputUseCase>
) : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    // required
    private val _title = MutableStateFlow("")
    val title = _title
        .onEach { checkSaveEnabled() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _title.value
        )

    private val _isTitleValid = MutableStateFlow(false)
    val isTitleValid = _isTitleValid
        .onEach { checkSaveEnabled() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _isTitleValid.value
        )

    private val _inputValidationText = MutableStateFlow("")
    val inputValidationText = _inputValidationText.stateIn(
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
    private val _instructions = MutableStateFlow<String?>(null)
    val instructions = _instructions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _instructions.value
    )

    private val _errorDialogState = MutableStateFlow(dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _errorDialogState.value
        )

    private val _saveEnabled = MutableStateFlow(false)
    val saveEnabled = _saveEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = _saveEnabled.value
    )


    fun onTitleChanged(text: String) {
        _title.update { text }
        viewModelScope.launch {
            // We don't show input validation text for empty input, just disable save
            if (text.isEmpty()) {
                _isTitleValid.update { false }
                _inputValidationText.update { "" }
            } else if (validateInputUseCase.get().recipeWithTitleAlreadyExists(text)) {
                _isTitleValid.update { false }
                _inputValidationText.update { "Recipe with this name already exists" }
            } else {
                // Valid input
                _isTitleValid.update { true }
                _inputValidationText.update { "" }
            }
        }
    }

    fun onReceiveIngredients(ingredients: List<RecipeIngredient>) {
        // In case the user wants to add more ingredients after adding some previously
        _ingredients += ingredients
        checkSaveEnabled()
    }

    fun onRemoveIngredient(index: Int) {
        _ingredients -= _ingredients[index]
        checkSaveEnabled()
    }

    fun onInstructionsChanged(text: String) {
        _instructions.update { text }
    }

    fun onImageChanged(text: String) {
        // TODO
    }

    fun onDoneClicked() {
        viewModelScope.launch {
            addRecipeUseCase.get().invoke(createPartialRecipe()).collect { result ->
                when (result.status) {
                    SUCCESS -> {
                        clearInputs()
                        // TODO: show snackbar?
                    }
                    ERROR -> {
                        _errorDialogState.update {
                            displayed(
                                title = "Error",
                                message = result.message.toString()
                            )
                        }
                    }
                }
            }
        }
    }

    fun dismissErrorDialog() {
        _errorDialogState.update { dismissed() }
    }

    private fun clearInputs() {
        _title.update { "" }
        _instructions.update { null }
        _ingredients.clear()
        _coverImage.update { "" }
    }

    private fun createPartialRecipe() = CustomRecipeInput(
        recipeTitle = _title.value,
        instructions = _instructions.value,
        ingredients = _ingredients,
        // TODO: revisit below when we allow submitting custom images
        recipeImage = _coverImage.value,
        recipeImageType = "",
    )

    private fun checkSaveEnabled() {
        _saveEnabled.update { _ingredients.size > 0 && _isTitleValid.value }
    }
}

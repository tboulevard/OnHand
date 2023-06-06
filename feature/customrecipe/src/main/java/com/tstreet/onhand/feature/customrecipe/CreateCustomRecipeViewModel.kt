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
import com.tstreet.onhand.feature.customrecipe.InputValidationState.Companion.hidden
import com.tstreet.onhand.feature.customrecipe.InputValidationState.Companion.shown
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

    // Required input fields
    private val _title = MutableStateFlow("")
    val title = _title
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _title.value
        )

    private val _ingredients = mutableStateListOf<RecipeIngredient>()
    val ingredients: List<RecipeIngredient> = _ingredients

    // Optional input fields
    // TODO: address in future PR
    private val _coverImage = MutableStateFlow("")
    val coverImage = _coverImage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _coverImage.value
    )

    private val _instructions = MutableStateFlow<String?>(null)
    val instructions = _instructions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _instructions.value
    )

    // Input state fields
    private val _errorDialogState = MutableStateFlow(dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _errorDialogState.value
        )

    private val _inputValidationState = MutableStateFlow(hidden())
    val inputValidationText = _inputValidationState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _inputValidationState.value
    )

    private val _saveEnabled = MutableStateFlow(false)
    val saveEnabled = _saveEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = _saveEnabled.value
    )

    private var isTitleValid = false
    private var ingredientSearchOpened = false

    private val _createdRecipeId = MutableStateFlow<Int?>(null)
    val createdRecipeId = _createdRecipeId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = _createdRecipeId.value
    )

    fun onTitleChanged(text: String) {
        viewModelScope.launch {
            _title.update { text }
            // We don't show input validation text for empty input, just disable save
            if (text.isEmpty()) {
                isTitleValid = false
                _inputValidationState.update { hidden() }
            } else if (validateInputUseCase.get().recipeAlreadyExists(text)) {
                isTitleValid = false
                _inputValidationState.update { shown("Recipe with this name already exists") }
            } else {
                // Valid input
                isTitleValid = true
                _inputValidationState.update { hidden() }
            }
            checkSaveEnabled()
        }
    }

    fun onReceiveIngredients(ingredients: List<RecipeIngredient>) {
        // Don't add received ingredients if we never opened ingredient search
        if (!ingredientSearchOpened) {
            return
        }

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
                when {
                    result.status == SUCCESS && result.data != null -> {
                        _createdRecipeId.update { result.data }
                    }
                    else -> {
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

    fun clear() {
        _title.update { "" }
        _instructions.update { null }
        _ingredients.clear()
        _coverImage.update { "" }
        ingredientSearchOpened = false
        isTitleValid = false
        checkSaveEnabled()
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
        _saveEnabled.update { _ingredients.size > 0 && isTitleValid }
    }

    fun resetRecipeId() {
        _createdRecipeId.update { null }
    }

    fun ingredientSearchOpened() {
        ingredientSearchOpened = true
    }
}

package com.tstreet.onhand.feature.customrecipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.Status.*
import com.tstreet.onhand.core.domain.usecase.customrecipe.AddRecipeUseCase
import com.tstreet.onhand.core.domain.usecase.customrecipe.CustomRecipeInputUseCase
import com.tstreet.onhand.core.model.CustomRecipeInput
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.RecipeIngredient
import com.tstreet.onhand.core.model.ui.SelectableIngredient
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import com.tstreet.onhand.core.ui.InputValidationState.Companion.hidden
import com.tstreet.onhand.core.ui.InputValidationState.Companion.shown
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class CreateCustomRecipeViewModel @Inject constructor(
    private val addRecipeUseCase: Provider<AddRecipeUseCase>,
    private val validateInputUseCase: Provider<CustomRecipeInputUseCase>
) : ViewModel() {

    init {
        Log.d("[OnHand]", "${this.javaClass.simpleName} created")
    }

    // Required input fields
    private val _title = MutableStateFlow("")
    val title = _title
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _title.value
        )

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

    private val _titleInputValidationState = MutableStateFlow(hidden())
    val titleInputValidationState = _titleInputValidationState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _titleInputValidationState.value
    )

    private val _isTitleValid = MutableStateFlow(false)
    val isTitleValid = _isTitleValid.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = _isTitleValid.value
    )

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
                _isTitleValid.update { false }
                _titleInputValidationState.update { hidden() }
            } else if (validateInputUseCase.get().recipeExists(text)) {
                _isTitleValid.update { false }
                _titleInputValidationState.update { shown("Recipe with this name already exists") }
            } else {
                // Valid input
                _isTitleValid.update { true }
                _titleInputValidationState.update { hidden() }
            }
        }
    }

    fun onInstructionsChanged(text: String) {
        _instructions.update { text }
    }

    fun onImageChanged(text: String) {
        // TODO
    }

    fun onSaveRecipe(ingredients: List<SelectableIngredient>) {
        viewModelScope.launch {
            addRecipeUseCase.get()
                .invoke(collectCustomRecipeInput(ingredients.map { it.ingredient }))
                .collect { result ->
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

    private fun collectCustomRecipeInput(ingredients: List<Ingredient>) =
        CustomRecipeInput(
            recipeTitle = _title.value,
            instructions = _instructions.value,
            ingredients = ingredients.map {
                RecipeIngredient(
                    ingredient = it,
                    amount = 0.0,
                    unit = ""
                )
            },
            // TODO: revisit below when we allow submitting custom images
            recipeImage = _coverImage.value,
            recipeImageType = "",
        )
}

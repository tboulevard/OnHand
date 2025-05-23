package com.tstreet.onhand.feature.customrecipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.model.CustomRecipeInput
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.model.ui.InputValidationState.Companion.hidden
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CreateCustomRecipeViewModel @Inject constructor() : ViewModel() {

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
    }

    fun onInstructionsChanged(text: String) {
        _instructions.update { text }
    }

    fun onImageChanged(text: String) {
        // TODO
    }

    fun onSaveRecipe(ingredients: List<Ingredient>) {
    }

    fun dismissErrorDialog() {
        _errorDialogState.update { dismissed() }
    }

    private fun collectCustomRecipeInput(ingredients: List<Ingredient>) =
        CustomRecipeInput(
            recipeTitle = _title.value,
            instructions = _instructions.value,
            ingredients = emptyList(),
            // TODO: revisit below when we allow submitting custom images
            recipeImage = _coverImage.value,
            recipeImageType = "",
        )
}

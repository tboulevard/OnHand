package com.tstreet.onhand.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.DEFAULT
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.Status.*
import com.tstreet.onhand.core.domain.usecase.ingredientsearch.IngredientSearchUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.GetPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.ui.PantryUiState
import com.tstreet.onhand.core.model.ui.SearchUiState
import com.tstreet.onhand.core.model.ui.UiPantryIngredient
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import kotlin.jvm.javaClass

@OptIn(kotlinx.coroutines.FlowPreview::class)
@FeatureScope
class HomeViewModel @Inject constructor(
    getPantry: Provider<GetPantryUseCase>,
    private val addToPantry: Provider<AddToPantryUseCase>,
    private val removeFromPantry: Provider<RemoveFromPantryUseCase>,
    ingredientSearchUseCase: Provider<IngredientSearchUseCase>,
    @Named(DEFAULT) private val dispatcher: CoroutineDispatcher,
    private val mapper: HomeUiStateMapper
) : ViewModel() {

    init {
        Log.d("[OnHand]", "${this.javaClass.simpleName} created")
    }

    val pantryUiState: StateFlow<PantryUiState> =
        getPantry.get().invoke()
            .map {
                mapper.mapPantryListResultToPantryUi(it)
            }
            .flowOn(dispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PantryUiState.Loading
            )

    val suggestedIngredientsUiState: StateFlow<SearchUiState> =
        ingredientSearchUseCase.get().getSuggestedIngredients()
            .map { result ->
                mapper.mapSuggestedIngredientsToSearchUiState(result)
            }.flowOn(dispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SearchUiState.Loading
            )

    private val _errorDialogState = MutableStateFlow(dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _errorDialogState.value
        )

    fun onToggleIngredient(pantryIngredient: UiPantryIngredient) {
        viewModelScope.launch {
            val inPantry = pantryIngredient.inPantry.value
            when {
                inPantry -> {
                    when (removeFromPantry.get().invoke(pantryIngredient.ingredient).status) {
                        SUCCESS -> {
                            pantryIngredient.inPantry.value = false
                        }

                        ERROR -> {
                            _errorDialogState.update {
                                displayed(
                                    title = "Error",
                                    message = "Unable to remove item from pantry. Please try again."
                                )
                            }
                        }
                    }
                }

                else -> {
                    when (addToPantry.get().invoke(pantryIngredient.ingredient).status) {
                        SUCCESS -> {
                            pantryIngredient.inPantry.value = true
                        }

                        ERROR -> {
                            _errorDialogState.update {
                                displayed(
                                    title = "Error",
                                    message = "Unable to add item to pantry. Please try again."
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun dismissErrorDialog() {
        _errorDialogState.update { dismissed() }
    }

    override fun onCleared() {
        // TODO: Not called, lifecycle not properly managed
        Log.d("[OnHand]", "$TAG cleared")
        super.onCleared()
    }
}

private val TAG = HomeViewModel::class.simpleName

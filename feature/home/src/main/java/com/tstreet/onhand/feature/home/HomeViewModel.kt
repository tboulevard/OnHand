package com.tstreet.onhand.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.DEFAULT
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.domain.usecase.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.GetPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.data.IngredientCategory
import com.tstreet.onhand.core.model.ui.home.HomeUiState
import com.tstreet.onhand.core.model.ui.home.SelectableIngredientCategory
import com.tstreet.onhand.core.model.ui.home.UiPantryIngredientV2
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@FeatureScope
class HomeViewModel @Inject constructor(
    private val getPantry: GetPantryUseCase,
    private val addToPantry: AddToPantryUseCase,
    private val removeFromPantry: RemoveFromPantryUseCase,
    private val mapper: HomeUiStateMapper,
    private val categoryFilterManager: CategoryFilterSelectionManager,
    @Named(DEFAULT) private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(filterCategories = categoryFilterManager.selectableCategories)
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPantry()
        viewModelScope.launch {
            categoryFilterManager.observeSelected {
                applyFilters(it)
            }.collect {

            }
        }

    }

    private val _event = Channel<HomeUiEvent>()
    val event = _event.receiveAsFlow()

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.ButtonClick.AddToPantry -> {
                addToPantry(event.ingredient)
            }

            is HomeUiEvent.ButtonClick.RemoveFromPantry -> {
                removeFromPantry(event.ingredient)
            }

            is HomeUiEvent.ButtonClick.CategoryFilter -> {
                selectPantryFilterCategory(event.category)
            }

            is HomeUiEvent.Navigation -> {
                handleNavigation(event)
            }

            else -> {
                Log.d("[OnHand]", "Unhandled onEvent: ${event.javaClass.simpleName}")
            }
        }
    }

    private fun loadPantry() {
        viewModelScope.launch {
            val result = getPantry(
                // TODO: Just select first category - change to support multi select later
                _uiState.value.filterCategories.first { it.isSelected.value }.category
            )

            val pantryRows = mapper.mapToPantryUi(result)

            _uiState.update { it.copy(pantryRows = pantryRows) }
        }
    }

    private fun applyFilters(filterCategory: IngredientCategory) {
        val filterCategories = _uiState.value.filterCategories
        filterCategories.find { it.category == filterCategory }?.isSelected?.value = true
        loadPantry()
    }

    private fun addToPantry(ingredient: UiPantryIngredientV2) {
        viewModelScope.launch {
            when (addToPantry(ingredient.ingredient).status) {
                SUCCESS -> {
                    _event.send(HomeUiEvent.ShowSnackbar("Added ${ingredient.ingredient.name} to pantry."))
                    ingredient.inPantry.value = true
                }

                ERROR -> {
                    _event.send(HomeUiEvent.ShowSnackbar("Error adding to pantry. Please try again."))
                }
            }
        }
    }

    private fun removeFromPantry(ingredient: UiPantryIngredientV2) {
        viewModelScope.launch {
            when (removeFromPantry(ingredient.ingredient).status) {
                SUCCESS -> {
                    _event.send(HomeUiEvent.ShowSnackbar("Removed ${ingredient.ingredient.name} from pantry."))
                    ingredient.inPantry.value = false
                }

                ERROR -> {
                    _event.send(HomeUiEvent.ShowSnackbar("Error removing from pantry. Please try again."))
                }
            }
        }
    }

    private fun handleNavigation(event: HomeUiEvent.Navigation) {
        when (event) {
            HomeUiEvent.Navigation.IngredientSearch -> {
                viewModelScope.launch {
                    _event.send(HomeUiEvent.Navigation.IngredientSearch)
                }
            }
        }
    }

    private fun selectPantryFilterCategory(category: SelectableIngredientCategory) {
        categoryFilterManager.onSelected(category)
    }
}
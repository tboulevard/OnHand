package com.tstreet.onhand.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.DEFAULT
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.domain.usecase.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.GetPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.ui.home.HomeUiState
import com.tstreet.onhand.core.model.ui.home.SelectableIngredientCategory
import com.tstreet.onhand.core.model.ui.home.UiPantryIngredientV2
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@FeatureScope
class HomeViewModel @Inject constructor(
    getPantry: GetPantryUseCase,
    private val addToPantry: AddToPantryUseCase,
    private val removeFromPantry: RemoveFromPantryUseCase,
    private val mapper: HomeUiStateMapper,
    private val categoryFilterManager: CategoryFilterSelectionManager,
    @Named(DEFAULT) private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    val filterCategories = categoryFilterManager.selectableCategories

    /**
     * TODO: DB state reflected immediately because we expose as a flow - remove this
     */
    val uiState: StateFlow<HomeUiState> =
        categoryFilterManager.observeSelected {
            getPantry(it)
        }.map { getPantryResult ->
            mapper.mapToHomeUiState(
                getPantryResult
            )
        }
            .flowOn(defaultDispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = HomeUiState.Loading
            )

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

    private fun addToPantry(ingredient: UiPantryIngredientV2) {
        viewModelScope.launch {
            val result = addToPantry(ingredient.ingredient)
            when (result.status) {
                Status.SUCCESS -> {
                    _event.send(HomeUiEvent.ShowSnackbar("Added ${ingredient.ingredient.name} to pantry."))
                    ingredient.inPantry.value = true
                }

                Status.ERROR -> {
                    _event.send(HomeUiEvent.ShowSnackbar("Error adding to pantry. Please try again."))
                }
            }
        }
    }

    private fun removeFromPantry(ingredient: UiPantryIngredientV2) {
        viewModelScope.launch {
            val result = removeFromPantry(ingredient.ingredient)
            when (result.status) {
                Status.SUCCESS -> {
                    _event.send(HomeUiEvent.ShowSnackbar("Removed ${ingredient.ingredient.name} from pantry."))
                    ingredient.inPantry.value = false
                }

                Status.ERROR -> {
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
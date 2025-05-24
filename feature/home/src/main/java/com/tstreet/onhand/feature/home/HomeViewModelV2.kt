package com.tstreet.onhand.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.DEFAULT
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.domain.usecase.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.GetPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.ui.home.HomeViewUiStateV2
import com.tstreet.onhand.core.model.ui.home.SelectableIngredientCategory
import com.tstreet.onhand.core.model.ui.home.SelectedIngredientCategoryState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Named

@FeatureScope
class HomeViewModelV2 @Inject constructor(
    getPantry: GetPantryUseCase,
    private val addToPantry: AddToPantryUseCase,
    private val removeFromPantry: RemoveFromPantryUseCase,
    private val mapper: HomeUiStateMapper,
    @Named(DEFAULT) private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _selectedCategoriesState = MutableStateFlow(SelectedIngredientCategoryState.default)

    val uiState: StateFlow<HomeViewUiStateV2> =
        _selectedCategoriesState.combine(getPantry()) { categoriesState, getPantryResult ->
            mapper.mapToHomeUiState(
                categoriesState,
                getPantryResult
            )
        }
            .flowOn(defaultDispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = HomeViewUiStateV2.Loading
            )

    fun onIngredientClick() {

    }

    fun onCategoryClick(selected: SelectableIngredientCategory) {
        val selectedCategoryState = _selectedCategoriesState.value
        val category = selectedCategoryState.getCategory(selected.category)
        category.isSelected.value = !category.isSelected.value

        // Problem is we emit the exact same object, so state change is not detected. We need to
        // emit each selected category instead somehow
        _selectedCategoriesState.tryEmit(selectedCategoryState)
    }
}
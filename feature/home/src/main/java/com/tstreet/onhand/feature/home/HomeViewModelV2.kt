package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.DEFAULT
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.domain.usecase.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.GetPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.data.IngredientCategory
import com.tstreet.onhand.core.model.ui.home.HomeViewUiStateV2
import com.tstreet.onhand.core.model.ui.home.SelectableIngredientCategory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Named

@FeatureScope
class HomeViewModelV2 @Inject constructor(
    getPantry: GetPantryUseCase,
    private val addToPantry: AddToPantryUseCase,
    private val removeFromPantry: RemoveFromPantryUseCase,
    private val mapper: HomeUiStateMapper,
    private val categories: SelectableFilterCategories,
    @Named(DEFAULT) private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    val filterCategories = categories.selectableCategories

    val uiState: StateFlow<HomeViewUiStateV2> =
        categories.observeSelected {
            getPantry(it)
        }.map { getPantryResult ->
            mapper.mapToHomeUiState(
                categories.selectableCategories,
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
        categories.onSelected(selected)
    }
}
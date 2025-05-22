package com.tstreet.onhand.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.DEFAULT
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.domain.usecase.pantry.GetPantryUseCase
import com.tstreet.onhand.core.model.ui.home.HomeViewUiStateV2
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Named

@FeatureScope
class HomeViewModelV2 @Inject constructor(
    getPantry: GetPantryUseCase,
    private val mapper: HomeUiStateMapper,
    @Named(DEFAULT) private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    val uiState: StateFlow<HomeViewUiStateV2> =
        getPantry()
            .map { getPantryResult ->
                mapper.mapToHomeUiState(getPantryResult)
            }
            .flowOn(defaultDispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = HomeViewUiStateV2.Loading
            )

    fun onIngredientClick() {

    }
}
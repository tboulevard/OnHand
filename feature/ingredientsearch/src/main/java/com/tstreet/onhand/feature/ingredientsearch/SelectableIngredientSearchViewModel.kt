package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.DEFAULT
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.domain.usecase.ingredientsearch.IngredientSearchUseCase
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.ui.UiSearchIngredient
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
// TODO: MVP - needs various improvements to support retaining selections with navigation
class SelectableIngredientSearchViewModel @Inject constructor(
    searchIngredients: Provider<IngredientSearchUseCase>,
    mapper: SearchUiStateMapper,
    @Named(DEFAULT) private val dispatcher: CoroutineDispatcher
) : IngredientSearchViewModel(searchIngredients, mapper, dispatcher) {

    val selectedIngredients = mutableStateListOf<Ingredient>()

    override fun onItemClick(item: UiSearchIngredient) {
        viewModelScope.launch {
            val isSelected = item.isSelected.value
            val ingredient = item.ingredient

            if (!isSelected && selectedIngredients.contains(ingredient)) {
                // Extra safeguards to not add duplicate ingredients to selected list -
                // We don't map previously selected ingredients to UiSearchIngredient currently
                // TODO: Map properly to retain selected state
                _errorDialogState.update {
                    displayed(
                        title = "Error",
                        message = "Unable to select item, already in list."
                    )
                }
            } else {
                if (isSelected) {
                    selectedIngredients.remove(ingredient)
                } else {
                    selectedIngredients.add(ingredient)
                }

                item.isSelected.value = !isSelected
            }
        }
    }
}

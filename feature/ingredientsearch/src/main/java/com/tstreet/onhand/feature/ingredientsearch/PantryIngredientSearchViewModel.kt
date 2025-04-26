package com.tstreet.onhand.feature.ingredientsearch

import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.domain.usecase.ingredientsearch.IngredientSearchUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.ui.UiSearchIngredient
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@FeatureScope
class PantryIngredientSearchViewModel @Inject constructor(
    val addToPantry: Provider<AddToPantryUseCase>,
    val removeFromPantry: Provider<RemoveFromPantryUseCase>,
    searchIngredients: Provider<IngredientSearchUseCase>,
    mapper: SearchUiStateMapper,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : IngredientSearchViewModel(searchIngredients, mapper, ioDispatcher) {

    override fun onItemClick(item: UiSearchIngredient) {
        viewModelScope.launch {
            val inPantry = item.inPantry.value
            when {
                inPantry -> {
                    when (removeFromPantry.get().invoke(item.ingredient).status) {
                        Status.SUCCESS -> {
                            item.inPantry.value = false
                        }

                        Status.ERROR -> {
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
                    when (addToPantry.get().invoke(item.ingredient).status) {
                        Status.SUCCESS -> {
                            item.inPantry.value = true
                        }

                        Status.ERROR -> {
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
}